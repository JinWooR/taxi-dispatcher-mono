package com.taxidispatcher.services.dispatcher.application.service;

import com.taxidispatcher.services.dispatcher.application.dto.request.CreateDispatchRequest;
import com.taxidispatcher.services.dispatcher.application.dto.request.UpdateDispatchStatusRequest;
import com.taxidispatcher.services.dispatcher.application.dto.response.DispatchResponse;
import com.taxidispatcher.services.dispatcher.domain.candidate.DispatchCandidate;
import com.taxidispatcher.services.dispatcher.domain.candidate.DispatchCandidateRepository;
import com.taxidispatcher.services.dispatcher.domain.candidate.DispatchCandidateStatus;
import com.taxidispatcher.services.dispatcher.domain.dispatch.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DispatchServiceImpl implements DispatchService {

    private final DispatchRepository dispatchRepository;
    private final DispatchCandidateRepository candidateRepository;
    private final DispatchCandidateRegistrationService candidateRegistrationService;

    @Override
    public DispatchResponse createDispatch(String customerId, CreateDispatchRequest request) {
        Location departure = new Location(
            request.getDepartureLatitude(),
            request.getDepartureLongitude(),
            request.getDepartureAddress()
        );
        Location arrival = new Location(
            request.getArrivalLatitude(),
            request.getArrivalLongitude(),
            request.getArrivalAddress()
        );

        // 1. Dispatch 저장 (JpaRepository 자체 트랜잭션)
        Dispatch dispatch = Dispatch.create(new CustomerId(customerId), departure, arrival);
        Dispatch saved = dispatchRepository.save(dispatch);

        // 2. 후보 기사 검색 + 등록 (단계마다 별도 트랜잭션)
        candidateRegistrationService.searchAndRegister(saved);

        return DispatchResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DispatchResponse> getDispatchesByCustomer(String customerId, Pageable pageable) {
        return dispatchRepository.findByCustomerId(new CustomerId(customerId), pageable)
            .map(DispatchResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DispatchResponse> getPendingDispatches(Pageable pageable) {
        return dispatchRepository.findByStatus(DispatchStatus.REQUESTED, pageable)
            .map(DispatchResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public DispatchResponse getDispatch(String dispatchId) {
        Dispatch dispatch = findDispatchById(dispatchId);
        return DispatchResponse.from(dispatch);
    }

    @Override
    @Transactional
    public DispatchResponse acceptDispatch(String dispatchId, String driverId) {
        DispatchId dispatchIdVO = new DispatchId(dispatchId);
        DriverId driverIdVO = new DriverId(driverId);

        // 비관적 락으로 배차 조회 (동시 승인 방지)
        Dispatch dispatch = dispatchRepository.findByIdForUpdate(dispatchIdVO)
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("배차를 찾을 수 없습니다: %s", dispatchId)
            ));

        // 배차 상태 변경 (REQUESTED → ASSIGNED)
        dispatch.assignDriver(driverIdVO);
        dispatch.updateStatus(DispatchStatus.ASSIGNED);
        Dispatch saved = dispatchRepository.save(dispatch);

        // 후보 상태 처리
        // - 해당 기사 → ACCEPTED
        // - 다른 REQUESTED 후보 → TIMEOUT
        List<DispatchCandidate> candidates = candidateRepository.findByDispatchId(dispatchIdVO);
        for (DispatchCandidate candidate : candidates) {
            if (candidate.getDriverId().equals(driverIdVO)) {
                candidate.updateStatus(DispatchCandidateStatus.ACCEPTED);
                candidateRepository.save(candidate);
            } else if (candidate.getStatus() == DispatchCandidateStatus.REQUESTED) {
                candidate.updateStatus(DispatchCandidateStatus.TIMEOUT);
                candidateRepository.save(candidate);
            }
        }

        return DispatchResponse.from(saved);
    }

    @Override
    @Transactional
    public DispatchResponse rejectDispatch(String dispatchId, String driverId) {
        DispatchId dispatchIdVO = new DispatchId(dispatchId);
        DriverId driverIdVO = new DriverId(driverId);

        // 해당 기사의 후보 상태만 REJECTED로 변경 (Dispatch 상태 변경 X)
        DispatchCandidate candidate = candidateRepository.findByDispatchIdAndDriverId(dispatchIdVO, driverIdVO)
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("배차 후보를 찾을 수 없습니다: dispatchId=%s, driverId=%s", dispatchId, driverId)
            ));
        candidate.updateStatus(DispatchCandidateStatus.REJECTED);
        candidateRepository.save(candidate);

        Dispatch dispatch = findDispatchById(dispatchId);
        return DispatchResponse.from(dispatch);
    }

    @Override
    @Transactional
    public DispatchResponse updateDispatchStatus(String dispatchId, UpdateDispatchStatusRequest request) {
        Dispatch dispatch = findDispatchById(dispatchId);
        dispatch.updateStatus(request.getStatus());
        Dispatch saved = dispatchRepository.save(dispatch);
        return DispatchResponse.from(saved);
    }

    private Dispatch findDispatchById(String dispatchId) {
        return dispatchRepository.findById(new DispatchId(dispatchId))
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("배차를 찾을 수 없습니다: %s", dispatchId)
            ));
    }
}
