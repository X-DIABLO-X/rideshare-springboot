package com.harshit.rideshare.controller;

import jakarta.validation.Valid;
import com.harshit.rideshare.dto.CreateRideRequest;
import com.harshit.rideshare.dto.RideResponse;
import com.harshit.rideshare.service.RideService;
import com.harshit.rideshare.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @PostMapping("/rides")
    public ResponseEntity<RideResponse> createRide(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody CreateRideRequest request
    ) {
        String userId = jwtUtil.extractUserId(token);
        return ResponseEntity.ok(rideService.createRide(userId, request));
    }

    @GetMapping("/user/rides")
    public ResponseEntity<List<RideResponse>> getUserRides(
            @RequestHeader("Authorization") String token
    ) {
        String userId = jwtUtil.extractUserId(token);
        return ResponseEntity.ok(rideService.getUserRides(userId));
    }

    @PostMapping("/rides/{rideId}/complete")
    public ResponseEntity<RideResponse> completeRide(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.completeRide(rideId));
    }

    @GetMapping("/rides/search")
    public ResponseEntity<List<RideResponse>> searchRides(@RequestParam String text) {
        return ResponseEntity.ok(rideService.searchRides(text));
    }

    @GetMapping("/rides/filter-distance")
    public ResponseEntity<List<RideResponse>> filterByDistance(
            @RequestParam double min,
            @RequestParam double max
    ) {
        return ResponseEntity.ok(rideService.filterByDistance(min, max));
    }

    @GetMapping("/rides/filter-date")
    public ResponseEntity<List<RideResponse>> filterByDate(
            @RequestParam String start,
            @RequestParam String end
    ) {
        return ResponseEntity.ok(rideService.filterByDate(
                LocalDate.parse(start),
                LocalDate.parse(end)
        ));
    }

    @GetMapping("/rides/sort")
    public ResponseEntity<List<RideResponse>> sortByFare(
            @RequestParam(defaultValue = "asc") String order
    ) {
        return ResponseEntity.ok(rideService.getRidesSortedByFare(order));
    }
}