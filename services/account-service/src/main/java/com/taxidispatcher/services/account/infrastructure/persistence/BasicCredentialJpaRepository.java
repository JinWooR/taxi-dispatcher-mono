package com.taxidispatcher.services.account.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BasicCredentialJpaRepository extends JpaRepository<BasicCredentialJpaEntity, Long> {

    Optional<BasicCredentialJpaEntity> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);
}
