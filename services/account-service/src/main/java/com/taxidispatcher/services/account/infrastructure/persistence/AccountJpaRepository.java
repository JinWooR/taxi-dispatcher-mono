package com.taxidispatcher.services.account.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

/**
 * Account Spring Data JPA Repository
 */
@Repository
public interface AccountJpaRepository extends JpaRepository<AccountJpaEntity, Long> {

    /**
     * accountId로 조회
     */
    Optional<AccountJpaEntity> findByAccountId(String accountId);

    /**
     * 로그인 ID로 계정 조회
     * INNER JOIN FETCH를 사용하여 N+1 쿼리 방지
     * TYPE(c)를 사용하여 BasicCredentialJpaEntity 타입만 필터링
     */
    @Query("""
        SELECT DISTINCT a FROM AccountJpaEntity a
        INNER JOIN FETCH a.credentials c
        WHERE TYPE(c) = BasicCredentialJpaEntity
        AND c.loginId = :loginId
    """)
    Optional<AccountJpaEntity> findByLoginId(@Param("loginId") String loginId);

    /**
     * accountId 존재 여부
     */
    boolean existsByAccountId(String accountId);

    /**
     * 로그인 ID 존재 여부
     * TYPE() 함수를 사용하여 BasicCredentialJpaEntity 타입 필터링
     */
    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
        FROM AccountJpaEntity a
        INNER JOIN a.credentials c
        WHERE TYPE(c) = BasicCredentialJpaEntity
        AND c.loginId = :loginId
    """)
    boolean existsByLoginId(@Param("loginId") String loginId);
}
