package com.taxidispatcher.services.account.domain.credential;

import com.taxidispatcher.services.account.domain.account.AccountId;

import java.util.Objects;

/**
 * 기본 인증 수단 (Email + Password)
 */
public class BasicCredential extends Credential {

    private String loginId;         // 로그인 아이디 (이메일)
    private String hashedPassword;  // 해시된 비밀번호

    public BasicCredential(CredentialId credentialId, AccountId accountId, String loginId, String hashedPassword) {
        super(credentialId, accountId);
        this.loginId = Objects.requireNonNull(loginId, "loginId cannot be null");
        this.hashedPassword = Objects.requireNonNull(hashedPassword, "hashedPassword cannot be null");
    }

    // JPA 생성자
    protected BasicCredential() {
        super();
    }

    public String getLoginId() {
        return loginId;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    /**
     * 비밀번호 변경
     */
    public void updatePassword(String newHashedPassword) {
        this.hashedPassword = Objects.requireNonNull(newHashedPassword);
    }

    /**
     * 비밀번호 일치 여부 확인 (실제 구현에서는 PasswordEncoder 사용)
     * 이 메서드는 도메인 로직이 아니므로 실제로는 애플리케이션 계층에서 처리
     */
    public boolean matchPassword(String rawPassword, PasswordMatcher matcher) {
        return matcher.matches(rawPassword, this.hashedPassword);
    }

    @Override
    public String getType() {
        return "BASIC";
    }

    @FunctionalInterface
    public interface PasswordMatcher {
        boolean matches(String rawPassword, String encodedPassword);
    }
}
