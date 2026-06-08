package com.taxidispatcher.services.movementhistory.domain.segment;

import java.util.Objects;
import lombok.Getter;

@Getter
public class EncodedPolyline {
    private final String value;

    public EncodedPolyline(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("encoded polyline 은 빈 값일 수 없습니다.");
        }
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EncodedPolyline that = (EncodedPolyline) o;
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
