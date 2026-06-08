package com.taxidispatcher.services.movementhistory.domain.segment;

import java.util.Objects;
import lombok.Getter;

@Getter
public class WorkSessionId {
    private final String value;

    public WorkSessionId(String value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkSessionId that = (WorkSessionId) o;
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
