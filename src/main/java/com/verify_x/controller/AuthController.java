package com.verify_x.controller;

import com.verify_x.dto.AdminLoginRequestDto;
import com.verify_x.dto.CurrentUserDto;
import com.verify_x.dto.ForgotPasswordRequestDto;
import com.verify_x.dto.HrLoginResponseDto;
import com.verify_x.dto.LoginRequestDto;
import com.verify_x.dto.LoginResponseDto;
import com.verify_x.dto.ResetPasswordRequestDto;
import com.verify_x.dto.UserRegistrationDto;
import com.verify_x.dto.VerifyOtpRequestDto;
import com.verify_x.payload.ApiResponse;
import com.verify_x.services.AuthService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.BadCredentialsException;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springdoc.core.annotations.ParameterObject;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/candidateRegister")
    public ResponseEntity<ApiResponse<String>> registerUser(
            @Valid
            @ParameterObject
            @ModelAttribute UserRegistrationDto registrationDto) {

        String response = authService.register(registrationDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<String>builder()
                                .success(true)
                                .message("User registered successfully.")
                                .data(response)
                                .build()
                );
    }

    @PostMapping("/candidateLogin")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(
            @Valid @RequestBody LoginRequestDto loginRequestDto) {

        LoginResponseDto response =
                authService.login(loginRequestDto);

        return ResponseEntity.ok(
                ApiResponse.<LoginResponseDto>builder()
                        .success(true)
                        .message("Login successful.")
                        .data(response)
                        .build()
        );
    }

    //current user logged in
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CurrentUserDto>> currentUser() {

        return ResponseEntity.ok(
                ApiResponse.<CurrentUserDto>builder()
                        .success(true)
                        .message("Current user fetched successfully.")
                        .data(authService.getCurrentUser())
                        .build()
        );
    }

    @PostMapping("/verify-registration-otp")
    public ResponseEntity<ApiResponse<String>> verifyRegistrationOtp(
            @Valid @RequestBody VerifyOtpRequestDto request) {

        String response =
                authService.verifyRegistrationOtp(request);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("OTP verification successful.")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDto request) {

        String response =
                authService.initiatePasswordReset(
                        request.getEmail()
                );

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("If the email exists, an OTP has been sent.")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDto request) {

        String response =
                authService.resetPassword(request);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Password reset successful.")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/candidateLogout")
    public ResponseEntity<ApiResponse<String>> logout(
            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String authHeader) {

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            throw new BadCredentialsException(
                    "Invalid Authorization header."
            );
        }

        String token = authHeader.substring(7);

        authService.logout(token);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Logout successful.")
                        .data("Token invalidated successfully.")
                        .build()
        );
    }

    @PostMapping("/hrLogin")
    public ResponseEntity<ApiResponse<HrLoginResponseDto>> adminLogin(
            @Valid @RequestBody AdminLoginRequestDto request) {

        HrLoginResponseDto response =
                authService.adminLogin(request);

        return ResponseEntity.ok(
                ApiResponse.<HrLoginResponseDto>builder()
                        .success(true)
                        .message("Admin login successful.")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/hrLogout")
    public ResponseEntity<ApiResponse<String>> adminLogout(
            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String authHeader) {

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            throw new BadCredentialsException(
                    "Invalid Authorization header."
            );
        }

        String token = authHeader.substring(7);

        authService.adminLogout(token);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Admin logged out successfully.")
                        .data("Logout successful.")
                        .build()
        );
    }
}