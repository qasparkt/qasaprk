package com.qaspark.controller;

import com.qaspark.model.InquiryRequest;
import com.qaspark.model.InquiryResponse;
import com.qaspark.service.InquiryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class InquiryRestController {

    private final InquiryService inquiryService;

    public InquiryRestController(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    @PostMapping("/inquiry")
    public ResponseEntity<InquiryResponse> submitInquiry(@Valid @RequestBody InquiryRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new InquiryResponse(false, errorMsg, null));
        }

        InquiryRequest saved = inquiryService.saveInquiry(request);
        return ResponseEntity.ok(new InquiryResponse(
                true,
                "Thank you, " + saved.getName() + "! Your inquiry has been logged in our Java backend system. We will contact you at " + saved.getPhone() + " / " + saved.getEmail() + " within 24 hours.",
                saved.getId()
        ));
    }

    @GetMapping("/inquiries")
    public ResponseEntity<List<InquiryRequest>> getInquiries() {
        return ResponseEntity.ok(inquiryService.getAllInquiries());
    }
}
