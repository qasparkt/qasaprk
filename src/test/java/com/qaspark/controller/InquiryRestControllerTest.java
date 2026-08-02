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
 * Integration tests for InquiryRestController.
 * Covers: White-Box (code paths), Gray-Box (API contract), BVA, EP, Error Guessing.
 */
@SpringBootTest
@AutoConfigureMockMvc
class InquiryRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ===== SMOKE TEST =====
    @Test
    void smokeTest_getInquiries_returns200() throws Exception {
        mockMvc.perform(get("/api/inquiries"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    // ===== VALID INPUT (EP: valid partition) =====
    @Test
    void submitInquiry_validData_returns200() throws Exception {
        String body = """
                {
                  "name": "Puja Paul",
                  "email": "puja@example.com",
                  "phone": "9810438179",
                  "course": "Automation Testing",
                  "message": "I want to enroll"
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

    // ===== BVA: phone exactly 10 digits starting with 6-9 =====
    @Test
    void submitInquiry_phoneBoundaryMin_startsWith6_passes() throws Exception {
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
                .andExpect(status().isOk());
    }

    // ===== BVA: phone starting with 5 (below valid range) =====
    @Test
    void submitInquiry_phoneStartsWith5_returns400() throws Exception {
        String body = """
                {
                  "name": "Test User",
                  "email": "test@example.com",
                  "phone": "5000000000",
                  "course": "Manual",
                  "message": ""
                }
                """;
        mockMvc.perform(post("/api/inquiry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // ===== BVA: phone 9 digits (below boundary) =====
    @Test
    void submitInquiry_phone9Digits_returns400() throws Exception {
        String body = """
                {
                  "name": "Test User",
                  "email": "test@example.com",
                  "phone": "987654321",
                  "course": "Manual",
                  "message": ""
                }
                """;
        mockMvc.perform(post("/api/inquiry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // ===== BVA: phone 11 digits (above boundary) =====
    @Test
    void submitInquiry_phone11Digits_returns400() throws Exception {
        String body = """
                {
                  "name": "Test User",
                  "email": "test@example.com",
                  "phone": "98765432109",
                  "course": "Manual",
                  "message": ""
                }
                """;
        mockMvc.perform(post("/api/inquiry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // ===== EP: Invalid email format =====
    @Test
    void submitInquiry_invalidEmail_returns400() throws Exception {
        String body = """
                {
                  "name": "Test User",
                  "email": "notanemail",
                  "phone": "9876543210",
                  "course": "Manual",
                  "message": ""
                }
                """;
        mockMvc.perform(post("/api/inquiry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // ===== Error Guessing: missing required name =====
    @Test
    void submitInquiry_missingName_returns400() throws Exception {
        String body = """
                {
                  "name": "",
                  "email": "test@example.com",
                  "phone": "9876543210",
                  "course": "Manual",
                  "message": ""
                }
                """;
        mockMvc.perform(post("/api/inquiry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // ===== Error Guessing: empty request body =====
    @Test
    void submitInquiry_emptyBody_returns400() throws Exception {
        mockMvc.perform(post("/api/inquiry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    // ===== Integration: submitted inquiry appears in GET list =====
    @Test
    void submitInquiry_thenGetInquiries_containsEntry() throws Exception {
        String name = "UniqueTestUser_" + System.currentTimeMillis();
        String body = String.format("""
                {
                  "name": "%s",
                  "email": "unique@test.com",
                  "phone": "7000000000",
                  "course": "Database Testing",
                  "message": "integration test"
                }
                """, name);

        mockMvc.perform(post("/api/inquiry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/inquiries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == '" + name + "')]").isNotEmpty());
    }
}
