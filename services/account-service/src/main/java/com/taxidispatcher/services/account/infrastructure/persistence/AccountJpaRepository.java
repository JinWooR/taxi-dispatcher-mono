package com.taxidispatcher.services.account.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Account Spring Data JPA Repository
 */
@Repository
public interface AccountJpaRepository extends JpaRepository<AccountJpaEntity, String> {

    boolean existsById(String accountId);
}
