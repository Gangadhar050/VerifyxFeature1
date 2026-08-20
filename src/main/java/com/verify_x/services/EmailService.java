package com.verify_x.services;

public interface EmailService {

    void sendApplicationApprovedEmail(
            String to,
            String candidateName,
            String remarks
    );

    void sendApplicationRejectedEmail(
            String to,
            String candidateName,
            String remarks
    );

    void sendReUploadRequestEmail(
            String to,
            String candidateName,
            String remarks
    );
    void sendOtp(String email, String otp);
}