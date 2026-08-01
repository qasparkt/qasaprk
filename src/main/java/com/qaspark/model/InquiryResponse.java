package com.qaspark.model;

public class InquiryResponse {

    private boolean success;
    private String message;
    private String inquiryId;

    public InquiryResponse() {}

    public InquiryResponse(boolean success, String message, String inquiryId) {
        this.success = success;
        this.message = message;
        this.inquiryId = inquiryId;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getInquiryId() { return inquiryId; }
    public void setInquiryId(String inquiryId) { this.inquiryId = inquiryId; }
}
