package com.qaspark.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for PageController (Thymeleaf page routes).
 * Covers: Smoke, Sanity, System, UAT-style verification of page routing.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ===== SMOKE: All pages return HTTP 200 =====
    @Test
    void homePage_returns200() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    void indexPage_returns200() throws Exception {
        mockMvc.perform(get("/index"))
                .andExpect(status().isOk());
    }

    @Test
    void coursesPage_returns200() throws Exception {
        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk());
    }

    @Test
    void pricingPage_returns200() throws Exception {
        mockMvc.perform(get("/pricing"))
                .andExpect(status().isOk());
    }

    @Test
    void contactPage_returns200() throws Exception {
        mockMvc.perform(get("/contact"))
                .andExpect(status().isOk());
    }

    // ===== SANITY: .html URL aliases also work =====
    @Test
    void coursesHtml_returns200() throws Exception {
        mockMvc.perform(get("/courses.html"))
                .andExpect(status().isOk());
    }

    @Test
    void pricingHtml_returns200() throws Exception {
        mockMvc.perform(get("/pricing.html"))
                .andExpect(status().isOk());
    }

    @Test
    void contactHtml_returns200() throws Exception {
        mockMvc.perform(get("/contact.html"))
                .andExpect(status().isOk());
    }

    // ===== SYSTEM: Contact page accepts optional 'course' query param =====
    @Test
    void contactPage_withCourseParam_returns200() throws Exception {
        mockMvc.perform(get("/contact").param("course", "automation"))
                .andExpect(status().isOk());
    }

    @Test
    void contactPage_withManualCourseParam_returns200() throws Exception {
        mockMvc.perform(get("/contact.html").param("course", "manual"))
                .andExpect(status().isOk());
    }

    // ===== ERROR GUESSING: Unknown page should return 404 =====
    @Test
    void unknownPage_returns404() throws Exception {
        mockMvc.perform(get("/nonexistent-page"))
                .andExpect(status().isNotFound());
    }

    // ===== UAT-style: Content includes expected keywords =====
    @Test
    void homePage_containsQASparkBranding() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("QASpark")));
    }

    @Test
    void contactPage_containsContactForm() throws Exception {
        mockMvc.perform(get("/contact"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("contact-form")));
    }

    @Test
    void coursesPage_containsEnrollButton() throws Exception {
        mockMvc.perform(get("/courses"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Enroll")));
    }

    @Test
    void pricingPage_containsPricingInfo() throws Exception {
        mockMvc.perform(get("/pricing"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("EMI")));
    }
}
