package com.qaspark.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ReviewApiController.
 * Covers: White-Box, Gray-Box, BVA on star ratings, Smoke, Sanity, Regression.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ReviewApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ===== SMOKE: GET /api/reviews returns 200 =====
    @Test
    void smokeTest_getReviews_returns200() throws Exception {
        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    // ===== SANITY: Default reviews are pre-loaded =====
    @Test
    void getReviews_containsDefaultSeededReviews() throws Exception {
        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(3)))
                .andExpect(jsonPath("$[0].name").isNotEmpty())
                .andExpect(jsonPath("$[0].rating").isNumber());
    }

    // ===== VALID: Post a review with rating=5 (BVA: max boundary) =====
    @Test
    void submitReview_rating5_passes() throws Exception {
        String body = """
                {
                  "name": "Anjali Singh",
                  "role": "QA Analyst",
                  "rating": 5,
                  "reviewText": "Excellent training institute!"
                }
                """;
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ===== VALID: Post a review with rating=1 (BVA: min boundary) =====
    @Test
    void submitReview_rating1_passes() throws Exception {
        String body = """
                {
                  "name": "Test Reviewer",
                  "role": "Student",
                  "rating": 1,
                  "reviewText": "Needs some improvement"
                }
                """;
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    // ===== BVA: rating=0 (below min) should fail =====
    @Test
    void submitReview_rating0_returns400() throws Exception {
        String body = """
                {
                  "name": "Test Reviewer",
                  "role": "Student",
                  "rating": 0,
                  "reviewText": "Bad rating value"
                }
                """;
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // ===== BVA: rating=6 (above max) should fail =====
    @Test
    void submitReview_rating6_returns400() throws Exception {
        String body = """
                {
                  "name": "Test Reviewer",
                  "role": "Student",
                  "rating": 6,
                  "reviewText": "Too high a rating"
                }
                """;
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // ===== Error Guessing: empty review name =====
    @Test
    void submitReview_missingName_returns400() throws Exception {
        String body = """
                {
                  "name": "",
                  "role": "Student",
                  "rating": 4,
                  "reviewText": "Good course"
                }
                """;
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // ===== Error Guessing: empty reviewText =====
    @Test
    void submitReview_missingReviewText_returns400() throws Exception {
        String body = """
                {
                  "name": "Valid Name",
                  "role": "Student",
                  "rating": 4,
                  "reviewText": ""
                }
                """;
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // ===== Integration: submitted review appears in GET =====
    @Test
    void submitReview_thenGetReviews_containsNewReview() throws Exception {
        String uniqueName = "Regression_" + System.currentTimeMillis();
        String body = String.format("""
                {
                  "name": "%s",
                  "role": "SDET",
                  "rating": 5,
                  "reviewText": "This is a regression test review"
                }
                """, uniqueName);

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == '" + uniqueName + "')]").isNotEmpty());
    }
}
