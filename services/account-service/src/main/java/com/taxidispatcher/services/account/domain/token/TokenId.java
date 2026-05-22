package com.taxidispatcher.services.account.domain.token;

import java.util.Objects;
import java.util.UUID;

public class TokenId {

    private final String value;

    private TokenId(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static TokenId generate() {
        return new TokenId(UUID.randomUUID().toString());
    }

    public static TokenId of(String value) {
        return new TokenId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return value.equals(((TokenId) o).value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
