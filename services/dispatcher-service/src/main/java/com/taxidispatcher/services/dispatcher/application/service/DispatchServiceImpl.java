package com.taxidispatcher.services.dispatcher.application.service;

import com.taxidispatcher.services.dispatcher.application.dto.request.CreateDispatchRequest;
import com.taxidispatcher.services.dispatcher.application.dto.request.UpdateDispatchStatusRequest;
import com.taxidispatcher.services.dispatcher.application.dto.response.DispatchResponse;
import com.taxidispatcher.services.dispatcher.domain.dispatch.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DispatchServiceImpl implements DispatchService {

    private final DispatchRepository dispatchRepository;

    @Override
    public DispatchResponse createDispatch(String userId, CreateDispatchRequest request) {
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

        Dispatch dispatch = Dispatch.create(new UserId(userId), departure, arrival);
        Dispatch saved = dispatchRepository.save(dispatch);

        return DispatchResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DispatchResponse> getDispatchesByUser(String userId, Pageable pageable) {
        return dispatchRepository.findByUserId(new UserId(userId), pageable)
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
    public DispatchResponse acceptDispatch(String dispatchId, String driverId) {
        Dispatch dispatch = findDispatchById(dispatchId);
        dispatch.assignDriver(new DriverId(driverId));
        dispatch.updateStatus(DispatchStatus.ASSIGNED);
        Dispatch saved = dispatchRepository.save(dispatch);
        return DispatchResponse.from(saved);
    }

    @Override
    public DispatchResponse rejectDispatch(String dispatchId, String driverId) {
        // TODO: 거절 비즈니스 로직 확정 필요
        // 현재는 단순 조회만 (단일 기사의 거절은 배차 상태에 영향 없음)
        Dispatch dispatch = findDispatchById(dispatchId);
        return DispatchResponse.from(dispatch);
    }

    @Override
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
