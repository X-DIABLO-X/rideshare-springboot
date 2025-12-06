package com.harshit.rideshare.controller;

import jakarta.validation.Valid;
import com.harshit.rideshare.dto.CreateRideRequest;
import com.harshit.rideshare.dto.RideResponse;
import com.harshit.rideshare.service.RideService;
import com.harshit.rideshare.service.UserService;
import com.harshit.rideshare.config.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserRideController {

    private final RideService rideService;
    private final JwtUtil jwtUtil;

    public UserRideController(RideService rideService, JwtUtil jwtUtil) {
        this.rideService = rideService;
        this.jwtUtil = jwtUtil;
    }

    // Passenger creates a ride
    @PostMapping("/rides")
    public ResponseEntity<RideResponse> createRide(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody CreateRideRequest request
    ) {
        String userId = jwtUtil.extractUserId(token);
        return ResponseEntity.ok(rideService.createRide(userId, request));
    }

    // Passenger gets their rides
    @GetMapping("/user/rides")
    public ResponseEntity<List<RideResponse>> getUserRides(
            @RequestHeader("Authorization") String token
    ) {
        String userId = jwtUtil.extractUserId(token);
        return ResponseEntity.ok(rideService.getUserRides(userId));
    }

    // Complete ride (user or driver)
    @PostMapping("/rides/{rideId}/complete")
    public ResponseEntity<RideResponse> completeRide(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.completeRide(rideId));
    }
}
