package com.taxidispatcher.services.account.domain.credential;

/**
 * OAuth 인증 종류
 */
public enum OAuthKind {
    GOOGLE("Google"),
    APPLE("Apple"),
    KAKAO("카카오"),
    NAVER("네이버"),
    GITHUB("GitHub");

    private final String displayName;

    OAuthKind(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
