package com.qaspark.controller;

import com.qaspark.model.ReviewRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewApiController {

    private final List<ReviewRequest> reviews = new CopyOnWriteArrayList<>();

    public ReviewApiController() {
        reviews.add(new ReviewRequest("REV-001", "Priya Sharma", "QA Engineer @ TechCorp", 5, "Puja Ma'am and Raj Sir are incredible mentors! The 2-day free demo class convinced me immediately. Placed as QA Engineer!"));
        reviews.add(new ReviewRequest("REV-002", "Rahul Verma", "SDET @ Infosys", 5, "The database testing and Java automation live classes were crystal clear. The weekly mock interviews helped me remove all fear of interviews. Placed at Infosys!"));
        reviews.add(new ReviewRequest("REV-003", "Anjali Patel", "QA Analyst @ Wipro", 5, "Personalized 1-on-1 guidance is QASpark's biggest strength. They review your resume, conduct weekly mock interviews, and refer you to companies. Highly recommended!"));
    }

    @GetMapping
    public ResponseEntity<List<ReviewRequest>> getAllReviews() {
        return ResponseEntity.ok(reviews);
    }

    @PostMapping
    public ResponseEntity<?> submitReview(@Valid @RequestBody ReviewRequest request) {
        request.setId("REV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        reviews.add(0, request);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Thank you! Your review has been published.",
            "review", request
        ));
    }
}
