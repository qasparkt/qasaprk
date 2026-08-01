package com.qaspark.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class ReviewRequest {
    private String id;

    @NotBlank(message = "Name is required")
    private String name;

    private String role;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private int rating;

    @NotBlank(message = "Review text is required")
    private String reviewText;

    private String timestamp;

    public ReviewRequest() {
        this.timestamp = LocalDateTime.now().toString();
    }

    public ReviewRequest(String id, String name, String role, int rating, String reviewText) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.rating = rating;
        this.reviewText = reviewText;
        this.timestamp = LocalDateTime.now().toString();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
