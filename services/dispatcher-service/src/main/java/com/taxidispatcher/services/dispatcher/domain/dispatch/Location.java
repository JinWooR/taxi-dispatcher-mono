package com.taxidispatcher.services.dispatcher.domain.dispatch;

import lombok.Getter;

@Getter
public class Location {
    private final double latitude;
    private final double longitude;
    private final String address;

    public Location(double latitude, double longitude, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }
}
