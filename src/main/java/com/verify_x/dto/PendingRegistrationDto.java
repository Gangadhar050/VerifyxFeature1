package com.verify_x.dto;

import com.verify_x.enums.AppliedRole;
import com.verify_x.enums.CandidateType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingRegistrationDto {
    private String username;
    private String email;
    private String phoneNumber;
    private String password;
    private AppliedRole appliedRole;
    private CandidateType candidateType;
    private String emailOtpHash;
    private String mobileOtpHash;
    private LocalDateTime otpExpiresAt;
}