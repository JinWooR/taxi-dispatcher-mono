package com.taxidispatcher.services.dispatcher.application.service;

import com.taxidispatcher.services.dispatcher.domain.dispatch.Dispatch;
import com.taxidispatcher.services.dispatcher.domain.dispatch.DispatchRepository;
import com.taxidispatcher.services.dispatcher.domain.dispatch.DispatchStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 배차 만료 처리 스케줄러
 * 5초마다 REQUESTED 상태 Dispatch를 검사하여:
 *  - 현재 단계 시간 초과 → REQUESTED 후보를 TIMEOUT 처리 + 다음 단계 진행
 *  - 모든 후보가 응답 완료 (REJECTED) → 즉시 다음 단계 진행
 *  - 모든 단계 소진 → FAILED 처리
 *
 * 단일 서버 가정 (분산 환경 미고려, Kafka 도입 시 전환 예정)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DispatchExpirationScheduler {

    private final DispatchRepository dispatchRepository;
    private final DispatchCandidateRegistrationService registrationService;

    @Scheduled(fixedDelay = 5000)
    public void checkExpiredDispatches() {
        List<Dispatch> pendingDispatches = dispatchRepository.findAllByStatus(DispatchStatus.REQUESTED);

        for (Dispatch dispatch : pendingDispatches) {
            try {
                handleDispatch(dispatch);
            } catch (Exception e) {
                log.error("배차 만료 처리 실패: dispatchId={}", dispatch.getDispatchId(), e);
            }
        }
    }

    private void handleDispatch(Dispatch dispatch) {
        boolean expired = dispatch.getSearchScope().isExpired();
        boolean allResolved = registrationService.allCandidatesResolved(dispatch);

        if (expired) {
            // 단계 시간 초과: REQUESTED 후보 → TIMEOUT, 다음 단계 진행
            registrationService.timeoutPendingCandidates(dispatch);
            registrationService.proceedFromExpiredScope(dispatch);
        } else if (allResolved) {
            // 모든 후보 응답 완료 (예: 모두 REJECTED): 즉시 다음 단계
            registrationService.proceedFromExpiredScope(dispatch);
        }
    }
}
