package com.verify_x.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyOtpRequestDto {

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Email OTP is required")
    private String emailOtp;

    // SMS OTP disabled. Kept as an optional field for backward compatibility.
    // private String mobileOtp;
}
