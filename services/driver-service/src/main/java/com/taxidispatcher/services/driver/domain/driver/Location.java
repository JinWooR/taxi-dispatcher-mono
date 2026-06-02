package com.taxidispatcher.services.driver.domain.driver;

import java.time.LocalDateTime;
import java.util.Objects;

public class Location {
    private final double latitude;
    private final double longitude;
    private final LocalDateTime updatedAt;

    private Location(double latitude, double longitude, LocalDateTime updatedAt) {
        validate(latitude, longitude);
        this.latitude = latitude;
        this.longitude = longitude;
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    public static Location of(double latitude, double longitude) {
        return new Location(latitude, longitude, LocalDateTime.now());
    }

    public static Location of(double latitude, double longitude, LocalDateTime updatedAt) {
        return new Location(latitude, longitude, updatedAt);
    }

    private void validate(double latitude, double longitude) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("위도는 -90 ~ 90 범위여야 합니다: " + latitude);
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("경도는 -180 ~ 180 범위여야 합니다: " + longitude);
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Double.compare(latitude, location.latitude) == 0
                && Double.compare(longitude, location.longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
}
