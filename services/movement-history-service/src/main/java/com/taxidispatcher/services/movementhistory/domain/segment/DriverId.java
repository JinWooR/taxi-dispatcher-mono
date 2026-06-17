package com.taxidispatcher.services.movementhistory.domain.segment;

import java.util.Objects;
import lombok.Getter;

@Getter
public class DriverId {
    private final String value;

    public DriverId(String value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DriverId driverId = (DriverId) o;
        return Objects.equals(value, driverId.value);
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
