package com.taxidispatcher.services.driver.domain.driver;

import java.util.Objects;
import java.util.UUID;

public class DriverId {
    private final String value;

    private DriverId(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static DriverId generate() {
        return new DriverId(UUID.randomUUID().toString());
    }

    public static DriverId of(String value) {
        return new DriverId(value);
    }

    public String getValue() {
        return value;
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
