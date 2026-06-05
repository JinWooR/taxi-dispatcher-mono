package com.taxidispatcher.services.dispatcher.infrastructure.persistence;

import com.taxidispatcher.services.dispatcher.domain.dispatch.DispatchStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DispatchJpaRepository extends JpaRepository<DispatchJpaEntity, String> {

    Page<DispatchJpaEntity> findByCustomerId(String customerId, Pageable pageable);

    Page<DispatchJpaEntity> findByDispatchStatus(DispatchStatus dispatchStatus, Pageable pageable);

    List<DispatchJpaEntity> findAllByDispatchStatus(DispatchStatus dispatchStatus);

    /**
     * 비관적 락(SELECT ... FOR UPDATE)으로 배차 조회
     * 다른 트랜잭션의 READ는 가능, WRITE는 락 점유 시에만 가능
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM DispatchJpaEntity d WHERE d.dispatchId = :dispatchId")
    Optional<DispatchJpaEntity> findByIdForUpdate(@Param("dispatchId") String dispatchId);
}
