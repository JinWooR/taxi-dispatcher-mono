package com.taxidispatcher.services.account.application.service;

import com.taxidispatcher.services.account.domain.account.Account;
import com.taxidispatcher.services.account.domain.account.AccountId;
import com.taxidispatcher.services.account.domain.account.AccountRepository;
import com.taxidispatcher.services.account.domain.account.exception.AccountException;
import com.taxidispatcher.services.account.domain.credential.BasicCredential;
import com.taxidispatcher.services.account.domain.credential.CredentialId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Account 애플리케이션 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 계정 등록 (회원가입)
     *
     * @param loginId 로그인 ID (이메일)
     * @param password 비밀번호 (평문)
     * @return 생성된 계정
     * @throws AccountException 중복된 로그인 ID인 경우
     */
    public Account registerAccount(String loginId, String password) {
        // 1. 중복 확인
        if (accountRepository.existsByLoginId(loginId)) {
            throw new AccountException("ACCOUNT_DUPLICATE_EMAIL", "이미 가입된 이메일입니다: " + loginId);
        }

        // 2. 계정 생성
        AccountId accountId = AccountId.generate();
        Account account = new Account(accountId);

        // 3. 기본 인증 수단 추가
        String hashedPassword = passwordEncoder.encode(password);
        BasicCredential basicCredential = new BasicCredential(
                CredentialId.generate(),
                accountId,
                loginId,
                hashedPassword
        );

        account.addBasicCredential(basicCredential);

        // 4. 저장
        return accountRepository.save(account);
    }

    /**
     * 로그인 (계정 조회)
     *
     * @param loginId 로그인 ID (이메일)
     * @param password 비밀번호 (평문)
     * @return 계정 정보
     * @throws AccountException 계정을 찾을 수 없거나 비밀번호가 일치하지 않는 경우
     */
    @Transactional(readOnly = true)
    public Account loginAccount(String loginId, String password) {
        // 1. 계정 조회
        Account account = accountRepository.findByLoginId(loginId)
                .orElseThrow(() -> new AccountException("ACCOUNT_NOT_FOUND", "계정을 찾을 수 없습니다: " + loginId));

        // 2. 계정 상태 확인
        if (!account.isActive()) {
            throw new AccountException("ACCOUNT_INACTIVE", "활성화되지 않은 계정입니다");
        }

        // 3. 기본 인증 수단 찾기
        BasicCredential basicCredential = account.findBasicCredential(loginId)
                .orElseThrow(() -> new AccountException("ACCOUNT_NOT_FOUND", "인증 수단을 찾을 수 없습니다"));

        // 4. 비밀번호 검증
        if (!passwordEncoder.matches(password, basicCredential.getHashedPassword())) {
            throw new AccountException("ACCOUNT_INVALID_PASSWORD", "비밀번호가 올바르지 않습니다");
        }

        // 5. 마지막 사용 시간 업데이트
        basicCredential.updateLastUsedAt();
        accountRepository.save(account);

        return account;
    }

    /**
     * 계정 조회
     */
    @Transactional(readOnly = true)
    public Account getAccount(AccountId accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountException("ACCOUNT_NOT_FOUND", "계정을 찾을 수 없습니다: " + accountId));
    }

    /**
     * 계정 잠금
     */
    public void lockAccount(AccountId accountId) {
        Account account = getAccount(accountId);
        account.lock();
        accountRepository.save(account);
        log.info("계정 잠금: {}", accountId);
    }

    /**
     * 계정 해제
     */
    public void unlockAccount(AccountId accountId) {
        Account account = getAccount(accountId);
        account.unlock();
        accountRepository.save(account);
        log.info("계정 잠금 해제: {}", accountId);
    }

    /**
     * 계정 삭제
     */
    public void deleteAccount(AccountId accountId) {
        Account account = getAccount(accountId);
        account.delete();
        accountRepository.save(account);
        log.info("계정 삭제: {}", accountId);
    }
}
