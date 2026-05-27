package com.taxidispatcher.services.dispatcher.infrastructure.persistence;

import com.taxidispatcher.services.dispatcher.domain.dispatch.Location;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationEmbedded {

    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Column(name = "longitude", nullable = false)
    private double longitude;

    @Column(name = "address", nullable = false)
    private String address;

    public static LocationEmbedded from(Location location) {
        return LocationEmbedded.builder()
            .latitude(location.getLatitude())
            .longitude(location.getLongitude())
            .address(location.getAddress())
            .build();
    }

    public Location toModel() {
        return new Location(latitude, longitude, address);
    }
}
