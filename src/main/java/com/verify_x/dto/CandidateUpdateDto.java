package com.verify_x.dto;

import com.verify_x.enums.AppliedRole;
import com.verify_x.enums.CandidateType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateUpdateDto {
    @NotBlank(message = "Full Name is required")
    private String username;

    @Email(message = "Invalid Email")
    @NotBlank(message = "Email is required")
    private String email;

    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Invalid Mobile Number"
    )
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    private String address;

    @Pattern(
            regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$",
            message = "Invalid PAN Number"
    )
    private String panNumber;

    @Pattern(
            regexp = "^\\d{12}$",
            message = "Aadhaar Number must contain 12 digits"
    )
    private String aadhaarNumber;

    @NotNull(message = "Applied Role is required")
    private AppliedRole appliedRole;

    @NotNull(message = "Candidate Type is required")
    private CandidateType candidateType;


}
