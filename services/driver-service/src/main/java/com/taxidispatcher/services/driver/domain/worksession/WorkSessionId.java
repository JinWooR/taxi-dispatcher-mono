package com.taxidispatcher.services.driver.domain.worksession;

import java.util.Objects;
import java.util.UUID;

public class WorkSessionId {
    private final String value;

    private WorkSessionId(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static WorkSessionId generate() {
        return new WorkSessionId(UUID.randomUUID().toString());
    }

    public static WorkSessionId of(String value) {
        return new WorkSessionId(value);
    }

    public String getValue() {
        return value;
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
