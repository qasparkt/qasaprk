package com.qaspark.service;

import com.qaspark.model.InquiryRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class InquiryService {

    private final List<InquiryRequest> inquiryList = new CopyOnWriteArrayList<>();

    public InquiryRequest saveInquiry(InquiryRequest request) {
        String id = "QAS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        request.setId(id);
        // Sanitize: strip spaces from phone (defense-in-depth)
        if (request.getPhone() != null) {
            request.setPhone(request.getPhone().replaceAll("\\s+", ""));
        }
        inquiryList.add(request);
        System.out.println("📩 [JAVA BACKEND] New Inquiry Received: " + id + " from " + request.getName() + " (" + request.getEmail() + ")");
        return request;
    }

    public List<InquiryRequest> getAllInquiries() {
        return Collections.unmodifiableList(new ArrayList<>(inquiryList));
    }

    public int getInquiryCount() {
        return inquiryList.size();
    }
}
