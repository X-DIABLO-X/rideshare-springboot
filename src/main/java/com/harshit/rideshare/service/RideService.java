package com.harshit.rideshare.service;

import com.harshit.rideshare.dto.CreateRideRequest;
import com.harshit.rideshare.dto.RideResponse;
import com.harshit.rideshare.exception.BadRequestException;
import com.harshit.rideshare.exception.NotFoundException;
import com.harshit.rideshare.model.Ride;
import com.harshit.rideshare.repository.RideRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final MongoTemplate mongoTemplate;
    private final Random random = new Random();

    public RideService(RideRepository rideRepository, MongoTemplate mongoTemplate) {
        this.rideRepository = rideRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public List<RideResponse> searchRides(String text) {
        Query query = new Query();

        query.addCriteria(new Criteria().orOperator(
                Criteria.where("pickupLocation").regex(text, "i"),
                Criteria.where("dropLocation").regex(text, "i")
        ));

        return mongoTemplate.find(query, Ride.class)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<RideResponse> filterByDistance(double min, double max) {
        Query query = new Query();
        query.addCriteria(Criteria.where("distance").gte(min).lte(max));

        return mongoTemplate.find(query, Ride.class)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<RideResponse> filterByDate(LocalDate startDate, LocalDate endDate) {
        Date start = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        Query query = new Query();
        query.addCriteria(Criteria.where("createdAt").gte(start).lt(end));

        return mongoTemplate.find(query, Ride.class)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<RideResponse> getRidesSortedByFare(String order) {
        Query query = new Query();
        Sort.Direction direction = order.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        query.with(Sort.by(direction, "fare"));

        query.addCriteria(Criteria.where("status").is("COMPLETED"));

        return mongoTemplate.find(query, Ride.class)
                .stream().map(this::toResponse).collect(Collectors.toList());
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
        return rideRepository.findByStatus("REQUESTED").stream().map(this::toResponse).collect(Collectors.toList());
    }

    public RideResponse acceptRide(String rideId, String driverId, String driverUsername) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new NotFoundException("Ride not found"));
        if (!ride.getStatus().equals("REQUESTED")) throw new BadRequestException("Ride is already accepted or completed");
        ride.setDriverId(driverId);
        ride.setDriverUsername(driverUsername);
        ride.setStatus("ACCEPTED");
        rideRepository.save(ride);
        return toResponse(ride);
    }

    public RideResponse completeRide(String rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new NotFoundException("Ride not found"));
        if (!ride.getStatus().equals("ACCEPTED")) throw new BadRequestException("Cannot complete ride that is not accepted");
        ride.setStatus("COMPLETED");
        double fare = 50 + (150 * random.nextDouble());
        ride.setFare(Math.round(fare * 100.0) / 100.0);
        double distance = 2 + (18 * random.nextDouble());
        ride.setDistance(Math.round(distance * 10.0) / 10.0);
        rideRepository.save(ride);
        return toResponse(ride);
    }

    public List<RideResponse> getUserRides(String userId) {
        return rideRepository.findByUserId(userId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    private RideResponse toResponse(Ride ride) {
        return RideResponse.builder()
                .id(ride.getId())
                .userId(ride.getUserId())
                .driverId(ride.getDriverId())
                .driverUsername(ride.getDriverUsername())
                .fare(ride.getFare())
                .distance(ride.getDistance())
                .pickupLocation(ride.getPickupLocation())
                .dropLocation(ride.getDropLocation())
                .status(ride.getStatus())
                .createdAt(ride.getCreatedAt().toString())
                .build();
    }
}