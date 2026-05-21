package com.taxidispatcher.services.dispatcher.domain.dispatch;

import java.util.UUID;
import java.util.Objects;
import lombok.Getter;

@Getter
public class DispatchId {
    private final String value;

    public DispatchId(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static DispatchId generate() {
        return new DispatchId(UUID.randomUUID().toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DispatchId that = (DispatchId) o;
        return Objects.equals(value, that.value);
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
