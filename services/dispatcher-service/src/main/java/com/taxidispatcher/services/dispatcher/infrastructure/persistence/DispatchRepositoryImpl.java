package com.taxidispatcher.services.dispatcher.infrastructure.persistence;

import com.taxidispatcher.services.dispatcher.domain.dispatch.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DispatchRepositoryImpl implements DispatchRepository {

    private final DispatchJpaRepository jpaRepository;

    @Override
    public Dispatch save(Dispatch dispatch) {
        DispatchJpaEntity entity = DispatchJpaEntity.from(dispatch);
        DispatchJpaEntity saved = jpaRepository.save(entity);
        return saved.toModel();
    }

    @Override
    public Optional<Dispatch> findById(DispatchId dispatchId) {
        return jpaRepository.findById(dispatchId.getValue())
            .map(DispatchJpaEntity::toModel);
    }

    @Override
    public Page<Dispatch> findByCustomerId(CustomerId customerId, Pageable pageable) {
        return jpaRepository.findByCustomerId(customerId.getValue(), pageable)
            .map(DispatchJpaEntity::toModel);
    }

    @Override
    public Page<Dispatch> findByStatus(DispatchStatus status, Pageable pageable) {
        return jpaRepository.findByDispatchStatus(status, pageable)
            .map(DispatchJpaEntity::toModel);
    }
}
