package com.taxidispatcher.services.customer.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * CustomerJpaEntity 조회 인터페이스 (Spring Data JPA)
 */
@Repository
public interface CustomerJpaRepository extends JpaRepository<CustomerJpaEntity, String> {

    Optional<CustomerJpaEntity> findByAccountId(String accountId);

    boolean existsByAccountId(String accountId);
}
