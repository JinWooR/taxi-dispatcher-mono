package com.taxidispatcher.services.dispatcher.domain.candidate;

import com.taxidispatcher.services.dispatcher.domain.dispatch.DispatchId;
import com.taxidispatcher.services.dispatcher.domain.dispatch.DriverId;

import java.util.List;
import java.util.Optional;

public interface DispatchCandidateRepository {
    DispatchCandidate save(DispatchCandidate candidate);

    List<DispatchCandidate> saveAll(List<DispatchCandidate> candidates);

    Optional<DispatchCandidate> findById(DispatchCandidateId candidateId);

    List<DispatchCandidate> findByDispatchId(DispatchId dispatchId);

    Optional<DispatchCandidate> findByDispatchIdAndDriverId(DispatchId dispatchId, DriverId driverId);
}
