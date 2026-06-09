package com.taxidispatcher.services.driver.infrastructure.persistence;

import com.taxidispatcher.services.driver.domain.worksession.WorkSession;
import com.taxidispatcher.services.driver.domain.worksession.WorkSessionId;
import com.taxidispatcher.services.driver.domain.worksession.WorkSessionRepository;
import com.taxidispatcher.services.driver.domain.worksession.WorkSessionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkSessionRepositoryImpl implements WorkSessionRepository {

    private final WorkSessionJpaRepository jpaRepository;

    @Override
    public WorkSession save(WorkSession workSession) {
        Optional<WorkSessionJpaEntity> existing =
                jpaRepository.findByWorkSessionId(workSession.getWorkSessionId().getValue());

        WorkSessionJpaEntity saved;
        if (existing.isPresent()) {
            WorkSessionJpaEntity existingEntity = existing.get();
            existingEntity.updateFromDomain(workSession);
            saved = jpaRepository.save(existingEntity);
        } else {
            saved = jpaRepository.save(WorkSessionJpaEntity.fromDomain(workSession));
        }
        return saved.toDomain();
    }

    @Override
    public Optional<WorkSession> findById(WorkSessionId workSessionId) {
        return jpaRepository.findByWorkSessionId(workSessionId.getValue())
                .map(WorkSessionJpaEntity::toDomain);
    }

    @Override
    public Optional<WorkSession> findInProgressByDriverId(String driverId) {
        return jpaRepository.findFirstByDriverIdAndStatus(driverId, WorkSessionStatus.IN_PROGRESS)
                .map(WorkSessionJpaEntity::toDomain);
    }

    @Override
    public Page<WorkSession> findByDriverId(String driverId, Pageable pageable) {
        return jpaRepository.findByDriverId(driverId, pageable)
                .map(WorkSessionJpaEntity::toDomain);
    }
}
