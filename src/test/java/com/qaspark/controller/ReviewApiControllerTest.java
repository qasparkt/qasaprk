package com.qaspark.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Deep Positive &amp; Negative Integration Tests for ReviewApiController.
 *
 * ── POSITIVE TESTS ──────────────────────────────────────────────────────────
 *   TC_REV_P01  Smoke: GET /api/reviews returns 200 + JSON
 *   TC_REV_P02  At least 3 seeded reviews are pre-loaded
 *   TC_REV_P03  BVA: rating=5 (max boundary) → 200, success=true
 *   TC_REV_P04  BVA: rating=1 (min boundary) → 200, success=true
 *   TC_REV_P05  Mid-range rating=3 → 200, success=true
 *   TC_REV_P06  Mid-range rating=4 → 200, success=true
 *   TC_REV_P07  Role field is optional → 200 without role
 *   TC_REV_P08  Response body contains id, success, message, review
 *   TC_REV_P09  Submitted review appears first in GET list (newest-first)
 *
 * ── NEGATIVE TESTS ──────────────────────────────────────────────────────────
 *   TC_REV_N01  BVA: rating=0 (below min) → 400, success=false
 *   TC_REV_N02  BVA: rating=6 (above max) → 400, success=false
 *   TC_REV_N03  BVA: rating=-1 (negative) → 400, success=false
 *   TC_REV_N04  Blank name → 400, success=false
 *   TC_REV_N05  Whitespace-only name → 400, success=false
 *   TC_REV_N06  Blank reviewText → 400, success=false
 *   TC_REV_N07  Whitespace-only reviewText → 400, success=false
 *   TC_REV_N08  Missing reviewText key entirely → 400
 *   TC_REV_N09  Empty JSON body {} → 400
 *   TC_REV_N10  Wrong Content-Type (text/plain) → 415
 *   TC_REV_N11  Malformed JSON → 400
 */
@SpringBootTest
@AutoConfigureMockMvc
class ReviewApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // =========================================================================
    // ✅ POSITIVE TESTS
    // =========================================================================

    /** TC_REV_P01 – Smoke: GET /api/reviews returns 200 + JSON */
    @Test
    void TC_REV_P01_smokeTest_getReviews_returns200() throws Exception {
        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    /** TC_REV_P02 – At least 3 seeded reviews are pre-loaded with expected fields */
    @Test
    void TC_REV_P02_getReviews_containsAtLeast3SeededReviews() throws Exception {
        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(3)))
                .andExpect(jsonPath("$[*].name").value(hasItem(notNullValue())))
                .andExpect(jsonPath("$[*].rating").value(hasItem(greaterThanOrEqualTo(1))));
    }

    /** TC_REV_P03 – BVA: rating=5 (max boundary) → 200, success=true, review in response */
    @Test
    void TC_REV_P03_submitReview_rating5_passes() throws Exception {
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
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.review").exists());
    }

    /** TC_REV_P04 – BVA: rating=1 (min boundary) → 200, success=true */
    @Test
    void TC_REV_P04_submitReview_rating1_passes() throws Exception {
        String body = """
                {
                  "name": "Test Reviewer",
                  "role": "Student",
                  "rating": 1,
                  "reviewText": "Needs some improvement."
                }
                """;
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    /** TC_REV_P05 – Mid-range rating=3 should be accepted */
    @Test
    void TC_REV_P05_submitReview_rating3_passes() throws Exception {
        String body = """
                {
                  "name": "Midrange Tester",
                  "role": "Intern",
                  "rating": 3,
                  "reviewText": "Good course with some areas to improve."
                }
                """;
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    /** TC_REV_P06 – Mid-range rating=4 should be accepted */
    @Test
    void TC_REV_P06_submitReview_rating4_passes() throws Exception {
        String body = """
                {
                  "name": "Another Reviewer",
                  "role": "SDET",
                  "rating": 4,
                  "reviewText": "Very good institute. Highly recommended!"
                }
                """;
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    /** TC_REV_P07 – Optional 'role' field omitted → still 200 */
    @Test
    void TC_REV_P07_submitReview_withoutRole_passes() throws Exception {
        String body = """
                {
                  "name": "No Role User",
                  "rating": 5,
                  "reviewText": "Great course even without specifying role!"
                }
                """;
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    /** TC_REV_P08 – Response body has all expected fields: success, message, review.id */
    @Test
    void TC_REV_P08_submitReview_responseBody_hasAllExpectedFields() throws Exception {
        String body = """
                {
                  "name": "BodyCheck User",
                  "role": "QA",
                  "rating": 5,
                  "reviewText": "Checking response structure."
                }
                """;
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(containsString("Thank you")))
                .andExpect(jsonPath("$.review.id").value(startsWith("REV-")))
                .andExpect(jsonPath("$.review.name").value("BodyCheck User"))
                .andExpect(jsonPath("$.review.rating").value(5));
    }

    /** TC_REV_P09 – Integration: submitted review appears first in GET list (newest-first ordering) */
    @Test
    void TC_REV_P09_submitReview_thenGetReviews_appearsFirst() throws Exception {
        String uniqueName = "Regression_" + System.currentTimeMillis();
        String body = String.format("""
                {
                  "name": "%s",
                  "role": "SDET",
                  "rating": 5,
                  "reviewText": "This is a regression test review."
                }
                """, uniqueName);

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(uniqueName)); // newest-first check
    }

    // =========================================================================
    // ❌ NEGATIVE TESTS
    // =========================================================================

    /** TC_REV_N01 – BVA: rating=0 (below min 1) → 400, success=false */
    @Test
    void TC_REV_N01_submitReview_rating0_returns400() throws Exception {
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    /** TC_REV_N02 – BVA: rating=6 (above max 5) → 400, success=false */
    @Test
    void TC_REV_N02_submitReview_rating6_returns400() throws Exception {
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    /** TC_REV_N03 – BVA: rating=-1 (negative) → 400, success=false */
    @Test
    void TC_REV_N03_submitReview_ratingNegative_returns400() throws Exception {
        String body = """
                {
                  "name": "Test Reviewer",
                  "role": "Student",
                  "rating": -1,
                  "reviewText": "Negative rating"
                }
                """;
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    /** TC_REV_N04 – Blank name → 400, success=false */
    @Test
    void TC_REV_N04_submitReview_blankName_returns400() throws Exception {
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Name is required"));
    }

    /** TC_REV_N05 – Whitespace-only name → 400, success=false (@NotBlank trims whitespace) */
    @Test
    void TC_REV_N05_submitReview_whitespaceOnlyName_returns400() throws Exception {
        String body = """
                {
                  "name": "   ",
                  "role": "Student",
                  "rating": 4,
                  "reviewText": "Good course"
                }
                """;
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    /** TC_REV_N06 – Blank reviewText → 400, success=false */
    @Test
    void TC_REV_N06_submitReview_blankReviewText_returns400() throws Exception {
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Review text is required"));
    }

    /** TC_REV_N07 – Whitespace-only reviewText → 400, success=false */
    @Test
    void TC_REV_N07_submitReview_whitespaceOnlyReviewText_returns400() throws Exception {
        String body = """
                {
                  "name": "Valid Name",
                  "role": "Student",
                  "rating": 4,
                  "reviewText": "    "
                }
                """;
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    /** TC_REV_N08 – Missing reviewText key entirely → 400 (field treated as null) */
    @Test
    void TC_REV_N08_submitReview_missingReviewTextKey_returns400() throws Exception {
        String body = """
                {
                  "name": "Valid Name",
                  "role": "Student",
                  "rating": 4
                }
                """;
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    /** TC_REV_N09 – Empty JSON body {} → 400 */
    @Test
    void TC_REV_N09_submitReview_emptyJsonBody_returns400() throws Exception {
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    /** TC_REV_N10 – Wrong Content-Type (text/plain) → 415 Unsupported Media Type */
    @Test
    void TC_REV_N10_submitReview_wrongContentType_returns415() throws Exception {
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("name=Test&rating=5&reviewText=Good"))
                .andExpect(status().isUnsupportedMediaType());
    }

    /** TC_REV_N11 – Malformed / truncated JSON → 400 */
    @Test
    void TC_REV_N11_submitReview_malformedJson_returns400() throws Exception {
        String malformedJson = "{ \"name\": \"Test\", \"rating\": ";
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
    }
}

