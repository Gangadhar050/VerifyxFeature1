package com.verify_x.dto;

import com.verify_x.entity.Candidate;
import com.verify_x.enums.AppliedRole;
import com.verify_x.enums.CandidateType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationDto {

    @NotBlank(message = "Full name is required")
    @Size(  min = 3,
            max = 100,
            message = "Full name must be between 3 and 100 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(  max = 150,
            message = "Email must not exceed 150 characters")
    private String email;


    @NotBlank(message = "Phone number is required")
    @Pattern( regexp = "^[6-9]\\d{9}$",
              message = "Phone number must contain exactly 10 digits")
    private String phoneNumber;


    @NotBlank(message = "Password is required")
    @Size(  min = 8,
            max = 30,
            message = "Password must be between 8 and 30 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).*$",
             message = "Password must contain uppercase, lowercase, number and special character")
    private String password;


    @NotNull(message = "Applied role is required")
    private AppliedRole appliedRole;



    @NotNull(message = "Candidate type is required")
    private CandidateType candidateType;
}