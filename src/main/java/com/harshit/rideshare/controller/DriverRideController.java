package com.harshit.rideshare.controller;

import com.harshit.rideshare.dto.RideResponse;
import com.harshit.rideshare.service.RideService;
import com.harshit.rideshare.config.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/driver")
public class DriverRideController {

    private final RideService rideService;
    private final JwtUtil jwtUtil;

    public DriverRideController(RideService rideService, JwtUtil jwtUtil) {
        this.rideService = rideService;
        this.jwtUtil = jwtUtil;
    }

    // Driver sees pending requests
    @GetMapping("/rides/requests")
    public ResponseEntity<List<RideResponse>> getPendingRides() {
        return ResponseEntity.ok(rideService.getPendingRides());
    }

    // Driver accepts ride
    @PostMapping("/rides/{rideId}/accept")
    public ResponseEntity<RideResponse> acceptRide(
            @RequestHeader("Authorization") String token,
            @PathVariable String rideId
    ) {
        String driverId = jwtUtil.extractUserId(token);
        return ResponseEntity.ok(rideService.acceptRide(rideId, driverId));
    }
}
