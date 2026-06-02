package com.taxidispatcher.services.dispatcher.domain.candidate;

import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Getter
public class DispatchCandidateId {
    private final String value;

    public DispatchCandidateId(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static DispatchCandidateId generate() {
        return new DispatchCandidateId(UUID.randomUUID().toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DispatchCandidateId that = (DispatchCandidateId) o;
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
