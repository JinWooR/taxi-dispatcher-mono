package com.taxidispatcher.services.driver.application.service;

import com.taxidispatcher.services.driver.domain.worksession.WorkSession;
import com.taxidispatcher.services.driver.domain.worksession.WorkSessionId;
import com.taxidispatcher.services.driver.domain.worksession.WorkSessionRepository;
import com.taxidispatcher.shared.common.dto.driver.internal.DriverInternalWorkSession;
import com.taxidispatcher.shared.common.exception.DomainException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WorkSessionService {

    private final WorkSessionRepository workSessionRepository;

    /**
     * ONLINE 진입 시 호출. 기존 IN_PROGRESS 세션이 있으면 강제 종료 후 신규 시작.
     */
    public WorkSession startNewSession(String driverId) {
        workSessionRepository.findInProgressByDriverId(driverId).ifPresent(existing -> {
            log.info("기존 IN_PROGRESS 세션 강제 종료: driverId={}, workSessionId={}",
                    driverId, existing.getWorkSessionId().getValue());
            existing.end();
            workSessionRepository.save(existing);
        });

        WorkSession newSession = WorkSession.start(driverId);
        WorkSession saved = workSessionRepository.save(newSession);
        log.info("신규 work-session 시작: driverId={}, workSessionId={}",
                driverId, saved.getWorkSessionId().getValue());
        return saved;
    }

    /**
     * OFFLINE 진입 시 호출. 현재 IN_PROGRESS 세션을 정상 종료.
     * IN_PROGRESS 세션이 없으면 no-op.
     */
    public void endCurrentSession(String driverId) {
        workSessionRepository.findInProgressByDriverId(driverId).ifPresent(session -> {
            session.end();
            workSessionRepository.save(session);
            log.info("work-session 종료: driverId={}, workSessionId={}",
                    driverId, session.getWorkSessionId().getValue());
        });
    }

    @Transactional(readOnly = true)
    public WorkSession findById(String workSessionId) {
        return workSessionRepository.findById(WorkSessionId.of(workSessionId))
                .orElseThrow(() -> new DomainException(
                        "WORK_SESSION_NOT_FOUND", "근무 세션을 찾을 수 없습니다", HttpStatus.NOT_FOUND));
    }

    /**
     * 내부 API: workSessionId로 근무 세션 프로필 조회 (서비스 간 통신용)
     */
    @Transactional(readOnly = true)
    public DriverInternalWorkSession findInternalProfileById(String workSessionId) {
        WorkSession workSession = findById(workSessionId);
        return toInternalProfile(workSession);
    }

    @Transactional(readOnly = true)
    public WorkSession findCurrentByDriverId(String driverId) {
        return workSessionRepository.findInProgressByDriverId(driverId)
                .orElseThrow(() -> new DomainException(
                        "WORK_SESSION_NOT_FOUND", "진행 중인 근무 세션이 없습니다", HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Page<WorkSession> findHistoryByDriverId(String driverId, Pageable pageable) {
        return workSessionRepository.findByDriverId(driverId, pageable);
    }

    private DriverInternalWorkSession toInternalProfile(WorkSession workSession) {
        return new DriverInternalWorkSession(
                workSession.getWorkSessionId().getValue(),
                workSession.getDriverId(),
                workSession.getStatus().name(),
                workSession.getStartedAt(),
                workSession.getEndedAt()
        );
    }
}
