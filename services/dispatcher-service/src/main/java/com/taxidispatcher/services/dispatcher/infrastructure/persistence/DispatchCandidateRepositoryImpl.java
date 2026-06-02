package com.taxidispatcher.services.dispatcher.infrastructure.persistence;

import com.taxidispatcher.services.dispatcher.domain.candidate.DispatchCandidate;
import com.taxidispatcher.services.dispatcher.domain.candidate.DispatchCandidateId;
import com.taxidispatcher.services.dispatcher.domain.candidate.DispatchCandidateRepository;
import com.taxidispatcher.services.dispatcher.domain.dispatch.DispatchId;
import com.taxidispatcher.services.dispatcher.domain.dispatch.DriverId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DispatchCandidateRepositoryImpl implements DispatchCandidateRepository {

    private final DispatchCandidateJpaRepository jpaRepository;

    @Override
    public DispatchCandidate save(DispatchCandidate candidate) {
        DispatchCandidateJpaEntity entity = DispatchCandidateJpaEntity.from(candidate);
        DispatchCandidateJpaEntity saved = jpaRepository.save(entity);
        return saved.toModel();
    }

    @Override
    public List<DispatchCandidate> saveAll(List<DispatchCandidate> candidates) {
        List<DispatchCandidateJpaEntity> entities = candidates.stream()
            .map(DispatchCandidateJpaEntity::from)
            .toList();
        return jpaRepository.saveAll(entities).stream()
            .map(DispatchCandidateJpaEntity::toModel)
            .toList();
    }

    @Override
    public Optional<DispatchCandidate> findById(DispatchCandidateId candidateId) {
        return jpaRepository.findById(candidateId.getValue())
            .map(DispatchCandidateJpaEntity::toModel);
    }

    @Override
    public List<DispatchCandidate> findByDispatchId(DispatchId dispatchId) {
        return jpaRepository.findByDispatchId(dispatchId.getValue()).stream()
            .map(DispatchCandidateJpaEntity::toModel)
            .toList();
    }

    @Override
    public Optional<DispatchCandidate> findByDispatchIdAndDriverId(DispatchId dispatchId, DriverId driverId) {
        return jpaRepository.findByDispatchIdAndDriverId(dispatchId.getValue(), driverId.getValue())
            .map(DispatchCandidateJpaEntity::toModel);
    }
}
