package com.taxidispatcher.services.dispatcher.domain.dispatch;

import java.util.Objects;
import lombok.Getter;

@Getter
public class CustomerId {
    private final String value;

    public CustomerId(String value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerId customerId = (CustomerId) o;
        return Objects.equals(value, customerId.value);
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
