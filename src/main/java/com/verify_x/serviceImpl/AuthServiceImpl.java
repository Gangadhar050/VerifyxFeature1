package com.verify_x.serviceImpl;

import com.verify_x.dto.*;
import com.verify_x.entity.Admin;
import com.verify_x.entity.Candidate;
import com.verify_x.entity.PendingRegistration;
import com.verify_x.enums.ApplicationStatus;
import com.verify_x.enums.Role;
import com.verify_x.exception.EmailAlreadyExistsException;
import com.verify_x.exception.UserAlreadyExistsException;
import com.verify_x.jwt.JwtService;
import com.verify_x.jwt.TokenBlacklist;
import com.verify_x.jwt.UserPrincipal;
import com.verify_x.repository.AdminRepository;
import com.verify_x.repository.CandidateRepository;
import com.verify_x.services.AuthService;
import com.verify_x.services.CandidateService;
import com.verify_x.services.EmailService;
import com.verify_x.services.PendingRegistrationService;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final CandidateRepository candidateRepository;

    private final PendingRegistrationService pendingRegistrationService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final CandidateService candidateService;

    private final AdminRepository adminRepository;

    private final TokenBlacklist tokenBlacklist;

    private final EmailService emailService;

    @Override
    public String register(UserRegistrationDto dto) {

        log.info("Registering candidate: {}", dto.getEmail());

        if (candidateRepository.existsByEmail(dto.getEmail())
                || pendingRegistrationService.existsByEmail(dto.getEmail())) {

            throw new EmailAlreadyExistsException(
                    "Email already exists."
            );
        }
        if (candidateRepository.existsByPhoneNumber(dto.getPhoneNumber())
                || pendingRegistrationService.existsByPhoneNumber(
                        dto.getPhoneNumber())) {

            throw new UserAlreadyExistsException(
                    "Phone number already exists."
            );
        }
        String emailOtp = generateOtp();
        String mobileOtp = generateOtp();

        PendingRegistration pending =
                PendingRegistration.builder()

                        .username(dto.getUsername())
                        .email(dto.getEmail())
                        .phoneNumber(dto.getPhoneNumber())
                        .password(passwordEncoder.encode(dto.getPassword()))
                        .appliedRole(dto.getAppliedRole().name())
                        .candidateType(dto.getCandidateType())
                        .emailOtpHash(passwordEncoder.encode(emailOtp))
//                        .mobileOtpHash(passwordEncoder.encode(mobileOtp))
                        .otpExpiresAt(
                                LocalDateTime.now()
                                        .plusMinutes(10))

                        .build();
        pendingRegistrationService.save(pending);
        emailService.sendOtp(dto.getEmail(), emailOtp);

        // Mobile OTP currently disabled
        // smsService.sendOtp(
        //         "+91" + dto.getPhoneNumber(),
        //         mobileOtp
        // );

        log.info(
                "Email and mobile OTP sent successfully to: {}",
                dto.getEmail()
        );

        return "OTP sent to email and mobile number. "
                + "Verify both OTPs to complete registration.";
    }

    @Override
    public String verifyRegistrationOtp(
            VerifyOtpRequestDto request) {

        PendingRegistration pending =
                pendingRegistrationService
                        .findByEmail(
                                request.getEmail()
                        )
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "Pending registration not found."
                                )
                        );

        if (pending.getOtpExpiresAt() == null
                || pending.getOtpExpiresAt()
                .isBefore(LocalDateTime.now())) {

            pendingRegistrationService.delete(
                    pending
            );

            throw new BadCredentialsException(
                    "OTP has expired. Please register again."
            );
        }

        boolean isEmailOtpValid =
                pending.getEmailOtpHash() != null
                        && request.getEmailOtp() != null
                        && passwordEncoder.matches(
                                request.getEmailOtp(),
                                pending.getEmailOtpHash()
                        );

        if (!isEmailOtpValid) {

            throw new BadCredentialsException(
                    "Invalid email OTP."
            );
        }

        Candidate candidate =
                Candidate.builder()

                        .username(pending.getUsername())
                        .email(pending.getEmail())

                        .phoneNumber(pending.getPhoneNumber())
                        .password(pending.getPassword())

                        .appliedRole(
                                Candidate.AppliedRole.valueOf(pending.getAppliedRole()))

                        .candidateType(pending.getCandidateType())
                        .role(Role.CANDIDATE)

                        .emailVerified(true)
                        .mobileVerified(true)
                        .enabled(true)

                        .applicationStatus(
                                ApplicationStatus.PENDING_VERIFICATION)

                        .build();

        Candidate savedCandidate = candidateRepository.save(candidate);

        candidateService.saveCandidateProfile(savedCandidate);

        pendingRegistrationService.delete(pending);

        log.info(
                "Candidate registration verified successfully. Candidate ID: {}",
                savedCandidate.getId());

        return "Registration verified successfully. Please log in.";
    }

    @Override
    public LoginResponseDto login(
            LoginRequestDto dto) {

        Candidate user =
                candidateRepository
                        .findByEmail(
                                dto.getEmail())
                        .orElseThrow(() ->
                                new BadCredentialsException(
                                        "Invalid email or password."));

        if (!user.isEnabled()
                || !user.isEmailVerified()
                || !user.isMobileVerified()) {

            throw new BadCredentialsException(
                    "Verify email and mobile OTP before logging in.");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()));
        String token =
                jwtService.generateToken(user);

        return LoginResponseDto.builder()

                .accessToken(token)

                .tokenType("Bearer")

                .userId(user.getId())

                .username(user.getUsername())

                .email(user.getEmail())

                .role(user.getRole())

                .candidateType(user.getCandidateType())

                .build();
    }

    @Override
    public CurrentUserDto getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new BadCredentialsException(
                    "User is not authenticated");
        }

        if (!(authentication.getPrincipal()
                instanceof UserPrincipal)) {

            throw new UsernameNotFoundException(
                    "User not found");
        }

        UserPrincipal principal =
                (UserPrincipal)
                        authentication.getPrincipal();

        Long userId =
                principal.getUserId();

        Candidate user =
                candidateRepository
                        .findById(
                                userId)
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "User not found"));

        return CurrentUserDto.builder()

                .userId(user.getId())

                .username(user.getUsername())

                .email(user.getEmail())

                .role(user.getRole())

                .candidateType(user.getCandidateType())

                .build();
    }

    @Override
    public HrLoginResponseDto adminLogin(
            AdminLoginRequestDto request) {


        Admin admin =
                adminRepository
                        .findByEmail(
                                request.getEmail()
                        )
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "Invalid credentials"
                                )
                        );

        if (admin.getRole() != Role.ADMIN) {

            throw new BadCredentialsException(
                    "Access denied");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));
        String token =
                jwtService.generateToken(
                        admin
                );
        return HrLoginResponseDto.builder()

                .accessToken(token)

                .tokenType("Bearer")

                .email(admin.getEmail())

                .role(admin.getRole())

                .build();
    }

    @Override
    public void adminLogout(
            String token) {

        if (token == null
                || token.isBlank()) {

            throw new IllegalArgumentException(
                    "Token is required.");
        }

        tokenBlacklist.blacklistToken(
                token);
        SecurityContextHolder.clearContext();

        log.info(
                "Admin logged out successfully.");
    }

    @Override
    public void logout(
            String token) {

        if (token == null
                || token.isBlank()) {

            throw new IllegalArgumentException(
                    "Token is required.");
        }
        tokenBlacklist.blacklistToken(
                token);

        SecurityContextHolder.clearContext();

        log.info(
                "User logged out successfully.");
    }

    @Override
    public String initiatePasswordReset(
            String email) {

        Candidate candidate =
                candidateRepository
                        .findByEmail(
                                email).orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "User not found."));

        if (!candidate.isEnabled()
                || !candidate.isEmailVerified()) {

            throw new BadCredentialsException(
                    "Account is not verified.");
        }
        String otp = generateOtp();

        candidate.setEmailOtpHash(
                passwordEncoder.encode(otp));
        candidate.setOtpExpiresAt(
                LocalDateTime.now()
                        .plusMinutes(10));

        candidateRepository.save(candidate);

        emailService.sendOtp(email, otp);

        log.info(
                "Password reset OTP sent to {}", email);

        return "OTP sent to email.";
    }

    @Override
    public String resetPassword(
            ResetPasswordRequestDto request) {

        Candidate candidate =
                candidateRepository
                        .findByEmail(
                                request.getEmail())
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "User not found."));

        if (candidate.getOtpExpiresAt() == null
                || candidate.getOtpExpiresAt()
                .isBefore(LocalDateTime.now())) {

            throw new BadCredentialsException(
                    "OTP has expired. Please request a new one."
            );
        }

        boolean isOtpValid =
                candidate.getEmailOtpHash() != null
                        && request.getEmailOtp() != null
                        && passwordEncoder.matches(
                                request.getEmailOtp(),
                                candidate.getEmailOtpHash()  );

        if (!isOtpValid) {

            throw new BadCredentialsException(
                    "Invalid email OTP."
            );
        }
        candidate.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()));

        candidate.setEmailOtpHash(null);

        candidate.setOtpExpiresAt(null);
        candidateRepository.save(candidate);

        log.info(
                "Password reset successful for {}",
                candidate.getEmail());

        return "Password has been reset successfully.";
    }
    private String generateOtp() {

        return String.format(
                "%06d",
                new SecureRandom()
                        .nextInt(1_000_000)
        );
    }
}