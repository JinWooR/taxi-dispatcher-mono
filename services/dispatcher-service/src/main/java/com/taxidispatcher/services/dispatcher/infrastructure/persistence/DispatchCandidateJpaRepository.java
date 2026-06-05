package com.taxidispatcher.services.dispatcher.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DispatchCandidateJpaRepository extends JpaRepository<DispatchCandidateJpaEntity, String> {

    List<DispatchCandidateJpaEntity> findByDispatchId(String dispatchId);

    Optional<DispatchCandidateJpaEntity> findByDispatchIdAndDriverId(String dispatchId, String driverId);
}
