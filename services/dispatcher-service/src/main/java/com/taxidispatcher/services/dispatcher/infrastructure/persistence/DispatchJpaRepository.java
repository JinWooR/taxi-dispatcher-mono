package com.taxidispatcher.services.dispatcher.infrastructure.persistence;

import com.taxidispatcher.services.dispatcher.domain.dispatch.DispatchStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DispatchJpaRepository extends JpaRepository<DispatchJpaEntity, String> {

    Page<DispatchJpaEntity> findByUserId(String userId, Pageable pageable);

    Page<DispatchJpaEntity> findByDispatchStatus(DispatchStatus dispatchStatus, Pageable pageable);
}
