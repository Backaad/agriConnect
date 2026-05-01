package com.agriconnect.labor.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @Column(name = "lat")
    private Double latitude;

    @Column(name = "lng")
    private Double longitude;

    public boolean isValid() {
        return latitude != null && longitude != null
            && latitude >= -90 && latitude <= 90
            && longitude >= -180 && longitude <= 180;
    }

    /**
     * Distance approximative en km (formule Haversine simplifiée)
     */
    public double distanceTo(Location other) {
        if (other == null || !other.isValid() || !this.isValid()) return Double.MAX_VALUE;
        double dLat = Math.toRadians(other.latitude - this.latitude);
        double dLon = Math.toRadians(other.longitude - this.longitude);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                 + Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(other.latitude))
                 * Math.sin(dLon/2) * Math.sin(dLon/2);
        return 6371 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    }
}
