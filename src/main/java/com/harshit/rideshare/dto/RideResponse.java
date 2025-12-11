package com.harshit.rideshare.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RideResponse {
    private String id;
    private String userId;
    private String driverId;

    private String pickupLocation;
    private String dropLocation;

    private String status;
    private String createdAt;

    private String driverUsername;
    private Double fare;
    private Double distance;
}
