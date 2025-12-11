package com.harshit.rideshare.service;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class AnalyticsService {

    private final MongoTemplate template;

    public AnalyticsService(MongoTemplate template) {
        this.template = template;
    }

    // 1. Calculate Total Earnings for a Driver
    public Double getDriverEarnings(String driverUsername) {
        // Match rides where driverUsername matches and status is COMPLETED
        MatchOperation match = match(
                Criteria.where("driverUsername").is(driverUsername)
                        .and("status").is("COMPLETED")
        );

        // Group by nothing (null) and sum the "fare" field
        GroupOperation group = group().sum("fare").as("total");

        Aggregation agg = newAggregation(match, group);

        // Run query
        Document result = template.aggregate(agg, "rides", Document.class)
                .getUniqueMappedResult();

        return result != null ? result.getDouble("total") : 0.0;
    }

    // 2. (Bonus) Count Rides by Status (e.g., how many COMPLETED vs REQUESTED)
    public Document getRideStatusSummary() {
        GroupOperation group = group("status").count().as("count");
        Aggregation agg = newAggregation(group);

        return template.aggregate(agg, "rides", Document.class).getRawResults();
    }
}