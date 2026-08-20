package com.verify_x.controller;

import com.verify_x.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class MailTestController {

    private final EmailService emailService;

    @GetMapping("/test-mail")
    public String testMail() {

        emailService.sendApplicationApprovedEmail(
                "gangadharhosamani050@gmail.com",
                "Gangadhar",
                "Mail Test");

        return "Mail Sent";
    }
}