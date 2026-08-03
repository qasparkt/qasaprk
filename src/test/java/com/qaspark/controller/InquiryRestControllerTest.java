package com.qaspark.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Positive &amp; Negative Integration Tests for InquiryRestController.
 *
 * ── POSITIVE TESTS ──────────────────────────────────────────────────────────
 *   TC_INQ_P01  Smoke: GET /api/inquiries returns 200 + JSON
 *   TC_INQ_P02  Valid full payload → 200, success=true, inquiryId returned
 *   TC_INQ_P03  BVA: phone starts with 6 (min valid prefix)
 *   TC_INQ_P04  BVA: phone starts with 9 (max valid prefix)
 *   TC_INQ_P05  Phone with spaced format "98765 43210" accepted
 *   TC_INQ_P06  Optional message field left empty → still 200
 *   TC_INQ_P07  Response message contains submitter's name and phone
 *   TC_INQ_P08  Integration: submitted inquiry appears in GET /api/inquiries
 *
 * ── NEGATIVE TESTS ──────────────────────────────────────────────────────────
 *   TC_INQ_N01  Missing name (blank) → 400
 *   TC_INQ_N02  Missing email (blank) → 400
 *   TC_INQ_N03  Invalid email format (no @) → 400
 *   TC_INQ_N04  Missing phone (blank) → 400
 *   TC_INQ_N05  BVA: phone 9 digits (below min) → 400
 *   TC_INQ_N06  BVA: phone 11 digits (above max) → 400
 *   TC_INQ_N07  BVA: phone starts with 5 (invalid prefix) → 400
 *   TC_INQ_N08  BVA: phone starts with 0 (invalid prefix) → 400
 *   TC_INQ_N09  Phone contains letters (alphanumeric) → 400
 *   TC_INQ_N10  Empty JSON body {} → 400
 *   TC_INQ_N11  Wrong Content-Type (text/plain) → 415
 *   TC_INQ_N12  Malformed JSON (syntax error) → 400
 */
@SpringBootTest
@AutoConfigureMockMvc
class InquiryRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // =========================================================================
    // ✅ POSITIVE TESTS
    // =========================================================================

    /** TC_INQ_P01 – Smoke: GET endpoint is reachable and returns JSON */
    @Test
    void TC_INQ_P01_smokeTest_getInquiries_returns200() throws Exception {
        mockMvc.perform(get("/api/inquiries"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    /** TC_INQ_P02 – Valid full payload → 200, success=true, inquiryId non-empty */
    @Test
    void TC_INQ_P02_submitInquiry_validFullPayload_returns200() throws Exception {
        String body = """
                {
                  "name": "Puja Paul",
                  "email": "puja@example.com",
                  "phone": "9810438179",
                  "course": "Automation Testing",
                  "message": "I want to enroll in the next batch."
                }
                """;
        mockMvc.perform(post("/api/inquiry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.inquiryId").isNotEmpty())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    /** TC_INQ_P03 – BVA: phone starting with 6 (minimum valid prefix) is accepted */
    @Test
    void TC_INQ_P03_submitInquiry_phoneBVA_startsWith6_passes() throws Exception {
        String body = """
                {
                  "name": "Raj Kumar",
                  "email": "raj@qaspark.in",
                  "phone": "6000000000",
                  "course": "Manual Testing",
                  "message": ""
                }
                """;
        mockMvc.perform(post("/api/inquiry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    /** TC_INQ_P04 – BVA: phone starting with 9 (maximum valid prefix) is accepted */
    @Test
    void TC_INQ_P04_submitInquiry_phoneBVA_startsWith9_passes() throws Exception {
        String body = """
                {
                  "name": "Anjali Sharma",
                  "email": "anjali@test.com",
                  "phone": "9999999999",
                  "course": "Database Testing",
                  "message": ""
                }
                """;
        mockMvc.perform(post("/api/inquiry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    /** TC_INQ_P05 – Phone in spaced format "98765 43210" should be accepted */
    @Test
    void TC_INQ_P05_submitInquiry_phoneSpacedFormat_passes() throws Exception {
        String body = """
                {
                  "name": "Sandeep Rao",
                  "email": "sandeep@example.com",
                  "phone": "98765 43210",
                  "course": "AI Testing",
                  "message": "Interested in AI-powered testing."
                }
                """;
        mockMvc.perform(post("/api/inquiry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    /** TC_INQ_P06 – Empty optional message should still allow a successful 200 response */
    @Test
    void TC_INQ_P06_submitInquiry_emptyOptionalMessage_passes() throws Exception {
        String body = """
                {
                  "name": "Priya Singh",
                  "email": "priya@test.com",
                  "phone": "7000000000",
                  "course": "Manual Testing",
                  "message": ""
                }
                """;
        mockMvc.perform(post("/api/inquiry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    /** TC_INQ_P07 – Response message must contain the submitter's name and phone */
    @Test
    void TC_INQ_P07_submitInquiry_responseMessage_containsNameAndPhone() throws Exception {
        String body = """
                {
                  "name": "TestUser",
                  "email": "testuser@example.com",
                  "phone": "8123456789",
                  "course": "Automation Testing",
                  "message": "Checking response content"
                }
                """;
        mockMvc.perform(post("/api/inquiry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(containsString("TestUser")))
                .andExpect(jsonPath("$.message").value(containsString("8123456789")));
    }

    /** TC_INQ_P08 – Integration: after POST, the inquiry must appear in GET /api/inquiries */
    @Test
    void TC_INQ_P08_submitInquiry_thenGetInquiries_containsEntry() throws Exception {
        String uniqueName = "UniqueUser_" + System.currentTimeMillis();
        String body = String.format("""
                {
                  "name": "%s",
                  "email": "unique@test.com",
                  "phone": "7000000000",
                  "course": "Database Testing",
                  "message": "integration test"
                }
                """, uniqueName);

        mockMvc.perform(post("/api/inquiry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/inquiries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == '" + uniqueName + "')]").isNotEmpty());
    }

    // =========================================================================
    // ❌ NEGATIVE TESTS
    // =========================================================================

    /** TC_INQ_N01 – Blank name should be rejected with 400 and success=false */
    @Test
    void TC_INQ_N01_submitInquiry_blankName_returns400() throws Exception {
        String body = """
                {
                  "name": "",
                  "email": "test@example.com",
                  "phone": "9876543210",
                  "course": "Manual Testing",
                  "message": ""
                }
                """;
        mockMvc.perform(post("/api/inquiry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    /** TC_INQ_N02 – Blank email should be rejected with 400 and success=false */
    @Test
    void TC_INQ_N02_submitInquiry_blankEmail_returns400() throws Exception {
        String body = """
                {
                  "name": "Test User",
                  "email": "",
                  "phone": "9876543210",
                  "course": "Manual Testing",
                  "message": ""
                }
                """;
        mockMvc.perform(post("/api/inquiry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    /** TC_INQ_N03 – Invalid email format (missing @) should be rejected with 400 */
    @Test
    void TC_INQ_N03_submitInquiry_invalidEmailFormat_returns400() throws Exception {
        String body = """
                {
                  "name": "Test User",
                  "email": "notanemail",
                  "phone": "9876543210",
                  "course": "Manual Testing",
                  "message": ""
                }
                """;
        mockMvc.perform(post("/api/inquiry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    /** TC_INQ_N04 – Blank phone should be rejected with 400 and success=false */
    @Test
    void TC_INQ_N04_submitInquiry_blankPhone_returns400() throws Exception {
        String body = """
                {
                  "name": "Test User",
                  "email": "test@example.com",
                  "phone": "",
                  "course": "Manual Testing",
                  "message": ""
                }
                """;
        mockMvc.perform(post("/api/inquiry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    /** TC_INQ_N05 – BVA: phone with 9 digits (below 10-digit minimum) is rejected */
    @Test
    void TC_INQ_N05_submitInquiry_phoneBVA_9digits_returns400() throws Exception {
        String body = """
                {
                  "name": "Test User",
                  "email": "test@example.com",
                  "phone": "987654321",
                  "course": "Manual Testing",
                  "message": ""
                }
                """;
        mockMvc.perform(post("/api/inquiry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    /** TC_INQ_N06 – BVA: phone with 11 digits (above 10-digit maximum) is rejected */
    @Test
    void TC_INQ_N06_submitInquiry_phoneBVA_11digits_returns400() throws Exception {
        String body = """
                {
                  "name": "Test User",
                  "email": "test@example.com",
                  "phone": "98765432109",
                  "course": "Manual Testing",
                  "message": ""
                }
                """;
        mockMvc.perform(post("/api/inquiry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    /** TC_INQ_N07 – BVA: phone starting with 5 (below valid range 6–9) is rejected */
    @Test
    void TC_INQ_N07_submitInquiry_phoneStartsWith5_returns400() throws Exception {
        String body = """
                {
                  "name": "Test User",
                  "email": "test@example.com",
                  "phone": "5000000000",
                  "course": "Manual Testing",
                  "message": ""
                }
                """;
        mockMvc.perform(post("/api/inquiry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    /** TC_INQ_N08 – BVA: phone starting with 0 (invalid prefix) is rejected */
    @Test
    void TC_INQ_N08_submitInquiry_phoneStartsWith0_returns400() throws Exception {
        String body = """
                {
                  "name": "Test User",
                  "email": "test@example.com",
                  "phone": "0000000000",
                  "course": "Manual Testing",
                  "message": ""
                }
                """;
        mockMvc.perform(post("/api/inquiry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    /** TC_INQ_N09 – Phone containing letters (alphanumeric) is rejected */
    @Test
    void TC_INQ_N09_submitInquiry_phoneWithLetters_returns400() throws Exception {
        String body = """
                {
                  "name": "Test User",
                  "email": "test@example.com",
                  "phone": "9876ABCDEF",
                  "course": "Manual Testing",
                  "message": ""
                }
                """;
        mockMvc.perform(post("/api/inquiry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    /** TC_INQ_N10 – Empty JSON body {} should fail all required-field validations → 400 */
    @Test
    void TC_INQ_N10_submitInquiry_emptyJsonBody_returns400() throws Exception {
        mockMvc.perform(post("/api/inquiry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    /** TC_INQ_N11 – Wrong Content-Type (text/plain instead of JSON) → 415 Unsupported Media Type */
    @Test
    void TC_INQ_N11_submitInquiry_wrongContentType_returns415() throws Exception {
        mockMvc.perform(post("/api/inquiry")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("name=Test&email=test@test.com&phone=9876543210"))
                .andExpect(status().isUnsupportedMediaType());
    }

    /** TC_INQ_N12 – Malformed / truncated JSON → 400 Bad Request */
    @Test
    void TC_INQ_N12_submitInquiry_malformedJson_returns400() throws Exception {
        // Intentionally truncated JSON to trigger a parse error
        String malformedJson = "{ \"name\": \"Test User\", \"email\": ";
        mockMvc.perform(post("/api/inquiry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
    }
}

