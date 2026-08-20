package com.verify_x.controller;

import com.verify_x.dto.VerificationQueueItemDto;
import com.verify_x.services.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/hr/verification")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','HR')")
public class VerificationController {

    private final VerificationService verificationService;

    @GetMapping("/queue")
    public ResponseEntity<List<VerificationQueueItemDto>> getVerificationQueue() {
        return ResponseEntity.ok(verificationService.getVerificationQueue());
    }
}
