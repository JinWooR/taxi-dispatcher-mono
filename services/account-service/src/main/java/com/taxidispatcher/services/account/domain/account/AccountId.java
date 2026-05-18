package com.taxidispatcher.services.account.domain.account;

import java.util.Objects;
import java.util.UUID;

/**
 * Account 식별자 (Value Object)
 */
public class AccountId {

    private final String value;

    private AccountId(String value) {
        this.value = Objects.requireNonNull(value, "AccountId value cannot be null");
    }

    /**
     * 새로운 AccountId 생성 (UUID)
     */
    public static AccountId generate() {
        return new AccountId(UUID.randomUUID().toString());
    }

    /**
     * 기존 AccountId 생성
     */
    public static AccountId of(String value) {
        return new AccountId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountId accountId = (AccountId) o;
        return Objects.equals(value, accountId.value);
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
