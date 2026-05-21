package com.taxidispatcher.services.account.domain.credential;

import com.taxidispatcher.services.account.domain.account.AccountId;

import java.util.Objects;

/**
 * OAuth 인증 수단 (SNS 로그인)
 */
public class OAuthCredential extends Credential {

    private OAuthKind oauthKind;    // 인증 제공자 (GOOGLE, APPLE, KAKAO 등)
    private String iss;             // 발급자 (issuer)
    private String sub;             // 발급 주체 (subject - 사용자 고유 ID)
    private String emailLink;       // 이메일 표기용

    public OAuthCredential(
            CredentialId credentialId,
            AccountId accountId,
            OAuthKind oauthKind,
            String iss,
            String sub,
            String emailLink
    ) {
        super(credentialId, accountId);
        this.oauthKind = Objects.requireNonNull(oauthKind, "oauthKind cannot be null");
        this.iss = Objects.requireNonNull(iss, "iss cannot be null");
        this.sub = Objects.requireNonNull(sub, "sub cannot be null");
        this.emailLink = emailLink; // nullable
    }

    // JPA 생성자
    protected OAuthCredential() {
        super();
    }

    public OAuthKind getOauthKind() {
        return oauthKind;
    }

    public String getIss() {
        return iss;
    }

    public String getSub() {
        return sub;
    }

    public String getEmailLink() {
        return emailLink;
    }

    /**
     * OAuth 인증 정보 검증
     * 발급자와 주체가 일치하는지 확인
     */
    public boolean validateOAuthInfo(String iss, String sub) {
        return this.iss.equals(iss) && this.sub.equals(sub);
    }

    @Override
    public String getType() {
        return "OAUTH";
    }
}
