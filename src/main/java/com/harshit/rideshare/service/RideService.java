package com.harshit.rideshare.service;

import com.harshit.rideshare.dto.CreateRideRequest;
import com.harshit.rideshare.dto.RideResponse;
import com.harshit.rideshare.exception.BadRequestException;
import com.harshit.rideshare.exception.NotFoundException;
import com.harshit.rideshare.model.Ride;
import com.harshit.rideshare.repository.RideRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RideService {

    private final RideRepository rideRepository;

    public RideService(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    public RideResponse createRide(String userId, CreateRideRequest request) {
        Ride ride = Ride.builder()
                .userId(userId)
                .pickupLocation(request.getPickupLocation())
                .dropLocation(request.getDropLocation())
                .status("REQUESTED")
                .createdAt(new Date())
                .build();

        rideRepository.save(ride);
        return toResponse(ride);
    }

    public List<RideResponse> getPendingRides() {
        return rideRepository.findByStatus("REQUESTED")
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public RideResponse acceptRide(String rideId, String driverId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new NotFoundException("Ride not found"));

        if (!ride.getStatus().equals("REQUESTED")) {
            throw new BadRequestException("Ride is already accepted or completed");
        }

        ride.setDriverId(driverId);
        ride.setStatus("ACCEPTED");
        rideRepository.save(ride);

        return toResponse(ride);
    }

    public RideResponse completeRide(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new NotFoundException("Ride not found"));

        if (!ride.getStatus().equals("ACCEPTED")) {
            throw new BadRequestException("Cannot complete ride that is not accepted");
        }

        ride.setStatus("COMPLETED");
        rideRepository.save(ride);

        return toResponse(ride);
    }

    public List<RideResponse> getUserRides(String userId) {
        return rideRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private RideResponse toResponse(Ride ride) {
        return RideResponse.builder()
                .id(ride.getId())
                .userId(ride.getUserId())
                .driverId(ride.getDriverId())
                .pickupLocation(ride.getPickupLocation())
                .dropLocation(ride.getDropLocation())
                .status(ride.getStatus())
                .createdAt(ride.getCreatedAt().toString())
                .build();
    }
}
