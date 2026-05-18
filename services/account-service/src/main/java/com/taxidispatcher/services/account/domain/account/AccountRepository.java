package com.taxidispatcher.services.account.domain.account;

import java.util.Optional;

/**
 * Account 저장소 인터페이스
 */
public interface AccountRepository {

    /**
     * 계정 저장
     */
    Account save(Account account);

    /**
     * 계정 ID로 조회
     */
    Optional<Account> findById(AccountId accountId);

    /**
     * 로그인 ID로 계정 조회
     */
    Optional<Account> findByLoginId(String loginId);

    /**
     * 계정 존재 여부 확인
     */
    boolean existsByAccountId(AccountId accountId);

    /**
     * 로그인 ID 존재 여부 확인
     */
    boolean existsByLoginId(String loginId);
}
