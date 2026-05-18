package com.taxidispatcher.services.account.domain.credential;

import java.util.Objects;
import java.util.UUID;

/**
 * Credential 식별자 (Value Object)
 */
public class CredentialId {

    private final String value;

    private CredentialId(String value) {
        this.value = Objects.requireNonNull(value, "CredentialId value cannot be null");
    }

    /**
     * 새로운 CredentialId 생성 (UUID)
     */
    public static CredentialId generate() {
        return new CredentialId(UUID.randomUUID().toString());
    }

    /**
     * 기존 CredentialId 생성
     */
    public static CredentialId of(String value) {
        return new CredentialId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CredentialId credentialId = (CredentialId) o;
        return Objects.equals(value, credentialId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
