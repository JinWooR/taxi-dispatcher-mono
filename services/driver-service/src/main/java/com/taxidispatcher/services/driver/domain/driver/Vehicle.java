package com.taxidispatcher.services.driver.domain.driver;

import java.util.Objects;

public class Vehicle {
    private final String plateNumber;
    private final String vehicleType;

    private Vehicle(String plateNumber, String vehicleType) {
        this.plateNumber = Objects.requireNonNull(plateNumber);
        this.vehicleType = Objects.requireNonNull(vehicleType);
    }

    public static Vehicle of(String plateNumber, String vehicleType) {
        return new Vehicle(plateNumber, vehicleType);
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(plateNumber, vehicle.plateNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plateNumber);
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "plateNumber='" + plateNumber + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                '}';
    }
}
