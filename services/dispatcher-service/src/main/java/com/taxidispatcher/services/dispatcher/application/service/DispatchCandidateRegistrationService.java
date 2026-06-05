package com.taxidispatcher.services.dispatcher.application.service;

import com.taxidispatcher.services.dispatcher.domain.candidate.DispatchCandidate;
import com.taxidispatcher.services.dispatcher.domain.candidate.DispatchCandidateRepository;
import com.taxidispatcher.services.dispatcher.domain.candidate.DispatchCandidateStatus;
import com.taxidispatcher.services.dispatcher.domain.dispatch.Dispatch;
import com.taxidispatcher.services.dispatcher.domain.dispatch.DispatchRepository;
import com.taxidispatcher.services.dispatcher.domain.dispatch.DispatchStatus;
import com.taxidispatcher.services.dispatcher.domain.dispatch.DriverId;
import com.taxidispatcher.services.dispatcher.infrastructure.client.DriverServiceClient;
import com.taxidispatcher.shared.common.dto.driver.internal.DriverInternalProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 배차 요청 시 출발지 주변 기사 검색 및 후보 등록 서비스
 * 단계별로 별도 트랜잭션이 적용됨 (1km → 3km → 5km)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchCandidateRegistrationService {

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000L;

    private final DispatchRepository dispatchRepository;
    private final DispatchCandidateRepository candidateRepository;
    private final DriverServiceClient driverServiceClient;

    // self-injection: 트랜잭션 AOP 프록시 호출용
    @Autowired
    @Lazy
    private DispatchCandidateRegistrationService self;

    /**
     * 단계별 후보 기사 검색 및 등록
     * 각 단계는 독립된 트랜잭션
     */
    public void searchAndRegister(Dispatch dispatch) {
        while (true) {
            boolean registered = self.trySearchAndRegisterInCurrentScope(dispatch);
            if (registered) {
                return;
            }

            if (!dispatch.getSearchScope().hasNextScope()) {
                self.markDispatchFailed(dispatch);
                return;
            }

            self.expandSearchScope(dispatch);
        }
    }

    /**
     * 현재 탐색 범위에서 후보 기사 검색 + 등록 (단일 트랜잭션)
     * 등록 성공 시 true, 0명 시 false 반환
     */
    @Transactional
    public boolean trySearchAndRegisterInCurrentScope(Dispatch dispatch) {
        int radiusKm = dispatch.getSearchScope().getCurrentRadiusKm();

        List<DriverInternalProfile> drivers = findNearbyDriversWithRetry(
            dispatch.getDeparture().getLatitude(),
            dispatch.getDeparture().getLongitude(),
            radiusKm
        );

        if (drivers.isEmpty()) {
            return false;
        }

        List<DispatchCandidate> candidates = drivers.stream()
            .map(d -> DispatchCandidate.create(
                dispatch.getDispatchId(),
                new DriverId(d.getDriverId())
            ))
            .toList();
        candidateRepository.saveAll(candidates);

        // TODO: 후보 기사들에게 알림 발송 (Kafka 도입 후 구현)

        return true;
    }

    /**
     * 다음 탐색 범위로 확장 (별도 트랜잭션)
     */
    @Transactional
    public void expandSearchScope(Dispatch dispatch) {
        dispatch.getSearchScope().expandScope();
        dispatchRepository.save(dispatch);
    }

    /**
     * 배차 실패 처리 (별도 트랜잭션)
     */
    @Transactional
    public void markDispatchFailed(Dispatch dispatch) {
        dispatch.updateStatus(DispatchStatus.FAILED);
        dispatchRepository.save(dispatch);
    }

    /**
     * 현재 단계의 REQUESTED 후보들을 TIMEOUT으로 변경 (별도 트랜잭션)
     */
    @Transactional
    public void timeoutPendingCandidates(Dispatch dispatch) {
        List<DispatchCandidate> candidates = candidateRepository.findByDispatchId(dispatch.getDispatchId());
        for (DispatchCandidate candidate : candidates) {
            if (candidate.getStatus() == DispatchCandidateStatus.REQUESTED) {
                candidate.updateStatus(DispatchCandidateStatus.TIMEOUT);
                candidateRepository.save(candidate);
            }
        }
    }

    /**
     * 현재 단계의 모든 후보가 응답 완료 (REQUESTED 없음) 여부 확인
     */
    public boolean allCandidatesResolved(Dispatch dispatch) {
        List<DispatchCandidate> candidates = candidateRepository.findByDispatchId(dispatch.getDispatchId());
        if (candidates.isEmpty()) {
            return false;
        }
        return candidates.stream()
            .noneMatch(c -> c.getStatus() == DispatchCandidateStatus.REQUESTED);
    }

    /**
     * 만료 또는 모든 후보 응답 완료 시 다음 단계로 진행
     */
    public void proceedFromExpiredScope(Dispatch dispatch) {
        if (!dispatch.getSearchScope().hasNextScope()) {
            self.markDispatchFailed(dispatch);
            return;
        }
        self.expandSearchScope(dispatch);
        searchAndRegister(dispatch);
    }

    /**
     * driver-service 호출 (3회 재시도)
     */
    private List<DriverInternalProfile> findNearbyDriversWithRetry(
        double latitude, double longitude, int radiusKm
    ) {
        int attempt = 0;
        Exception lastException = null;

        while (attempt < MAX_RETRIES) {
            try {
                return driverServiceClient.findNearbyDrivers(
                    latitude, longitude, (double) radiusKm, null
                );
            } catch (Exception e) {
                lastException = e;
                attempt++;
                log.warn("driver-service 호출 실패 {}/{}회: {}",
                    attempt, MAX_RETRIES, e.getMessage());

                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("재시도 대기 중 인터럽트", ie);
                    }
                }
            }
        }

        throw new RuntimeException(
            "driver-service " + MAX_RETRIES + "회 재시도 실패",
            lastException
        );
    }
}
