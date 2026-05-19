package com.taxidispatcher.services.user.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserJpaEntity 조회 인터페이스 (Spring Data JPA)
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, String> {

    Optional<UserJpaEntity> findByAccountId(String accountId);

    boolean existsByAccountId(String accountId);
}
