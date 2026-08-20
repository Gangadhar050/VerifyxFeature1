package com.verify_x.serviceTest;

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
import com.verify_x.serviceImpl.AuthServiceImpl;
import com.verify_x.services.CandidateService;
import com.verify_x.services.EmailService;
import com.verify_x.services.PendingRegistrationService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private PendingRegistrationService pendingRegistrationService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private CandidateService candidateService;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private TokenBlacklist tokenBlacklist;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }


    //register test cases
    @Test
    void register_shouldRegisterPendingUserSuccessfully() {

        UserRegistrationDto dto = mock(UserRegistrationDto.class);

        when(dto.getEmail()).thenReturn("test@gmail.com");
        when(dto.getPhoneNumber()).thenReturn("9876543210");
        when(dto.getUsername()).thenReturn("TestUser");
        when(dto.getPassword()).thenReturn("password");
        when(dto.getAppliedRole())
                .thenReturn(Candidate.AppliedRole.BACKEND_DEVELOPER);
        when(dto.getCandidateType()).thenReturn(null);

        when(candidateRepository.existsByEmail("test@gmail.com"))
                .thenReturn(false);

        when(pendingRegistrationService.existsByEmail("test@gmail.com"))
                .thenReturn(false);

        when(candidateRepository.existsByPhoneNumber("9876543210"))
                .thenReturn(false);

        when(pendingRegistrationService.existsByPhoneNumber("9876543210"))
                .thenReturn(false);

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encoded-value");

        String response = authService.register(dto);

        assertNotNull(response);

        assertTrue(
                response.contains("OTP sent")
        );

        verify(pendingRegistrationService)
                .save(any(PendingRegistration.class));

        verify(emailService)
                .sendOtp(
                        eq("test@gmail.com"),
                        anyString()
                );
    }


    @Test
    void register_shouldThrowException_whenEmailAlreadyExistsInCandidate() {

        UserRegistrationDto dto = mock(UserRegistrationDto.class);

        when(dto.getEmail())
                .thenReturn("existing@gmail.com");

        when(candidateRepository.existsByEmail("existing@gmail.com"))
                .thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> authService.register(dto)
        );

        verify(pendingRegistrationService, never())
                .save(any());

        verify(emailService, never())
                .sendOtp(anyString(), anyString());
    }


    @Test
    void register_shouldThrowException_whenEmailAlreadyExistsInPendingRegistration() {

        UserRegistrationDto dto = mock(UserRegistrationDto.class);

        when(dto.getEmail())
                .thenReturn("pending@gmail.com");

        when(candidateRepository.existsByEmail("pending@gmail.com"))
                .thenReturn(false);

        when(pendingRegistrationService.existsByEmail("pending@gmail.com"))
                .thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> authService.register(dto)
        );

        verify(pendingRegistrationService, never())
                .save(any());

        verify(emailService, never())
                .sendOtp(anyString(), anyString());
    }


    @Test
    void register_shouldThrowException_whenPhoneAlreadyExistsInCandidate() {

        UserRegistrationDto dto = mock(UserRegistrationDto.class);

        when(dto.getEmail())
                .thenReturn("test@gmail.com");

        when(dto.getPhoneNumber())
                .thenReturn("9876543210");

        when(candidateRepository.existsByEmail("test@gmail.com"))
                .thenReturn(false);

        when(pendingRegistrationService.existsByEmail("test@gmail.com"))
                .thenReturn(false);

        when(candidateRepository.existsByPhoneNumber("9876543210"))
                .thenReturn(true);

        assertThrows(
                UserAlreadyExistsException.class,
                () -> authService.register(dto)
        );

        verify(pendingRegistrationService, never())
                .save(any());
    }


    @Test
    void register_shouldThrowException_whenPhoneAlreadyExistsInPendingRegistration() {

        UserRegistrationDto dto = mock(UserRegistrationDto.class);

        when(dto.getEmail())
                .thenReturn("test@gmail.com");

        when(dto.getPhoneNumber())
                .thenReturn("9876543210");

        when(candidateRepository.existsByEmail("test@gmail.com"))
                .thenReturn(false);

        when(pendingRegistrationService.existsByEmail("test@gmail.com"))
                .thenReturn(false);

        when(candidateRepository.existsByPhoneNumber("9876543210"))
                .thenReturn(false);

        when(pendingRegistrationService.existsByPhoneNumber("9876543210"))
                .thenReturn(true);

        assertThrows(
                UserAlreadyExistsException.class,
                () -> authService.register(dto)
        );
    }


    //verify registration otp
    @Test
    void verifyRegistrationOtp_shouldVerifySuccessfully() {

        VerifyOtpRequestDto request =
                mock(VerifyOtpRequestDto.class);

        when(request.getEmail())
                .thenReturn("test@gmail.com");

        when(request.getEmailOtp())
                .thenReturn("123456");

        PendingRegistration pending =
                PendingRegistration.builder()
                        .username("TestUser")
                        .email("test@gmail.com")
                        .phoneNumber("9876543210")
                        .password("encoded-password")
                        .appliedRole(
                                Candidate.AppliedRole.BACKEND_DEVELOPER.name()
                        )
                        .candidateType(null)
                        .emailOtpHash("encoded-otp")
                        .otpExpiresAt(
                                LocalDateTime.now().plusMinutes(5)
                        )
                        .build();

        when(pendingRegistrationService.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(pending));

        when(passwordEncoder.matches(
                "123456",
                "encoded-otp"
        )).thenReturn(true);

        Candidate savedCandidate =
                Candidate.builder()
                        .id(100L)
                        .username("TestUser")
                        .email("test@gmail.com")
                        .phoneNumber("9876543210")
                        .role(Role.CANDIDATE)
                        .emailVerified(true)
                        .mobileVerified(true)
                        .enabled(true)
                        .applicationStatus(
                                ApplicationStatus.PENDING_VERIFICATION
                        )
                        .build();

        when(candidateRepository.save(any(Candidate.class)))
                .thenReturn(savedCandidate);

        String response =
                authService.verifyRegistrationOtp(request);

        assertEquals(
                "Registration verified successfully. Please log in.",
                response
        );

        verify(candidateRepository)
                .save(any(Candidate.class));

        verify(candidateService)
                .saveCandidateProfile(savedCandidate);

        verify(pendingRegistrationService)
                .delete(pending);
    }


    @Test
    void verifyRegistrationOtp_shouldThrowException_whenPendingRegistrationNotFound() {

        VerifyOtpRequestDto request =
                mock(VerifyOtpRequestDto.class);

        when(request.getEmail())
                .thenReturn("missing@gmail.com");

        when(pendingRegistrationService.findByEmail("missing@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> authService.verifyRegistrationOtp(request)
        );
    }


    @Test
    void verifyRegistrationOtp_shouldThrowException_whenOtpExpired() {

        VerifyOtpRequestDto request =
                mock(VerifyOtpRequestDto.class);

        when(request.getEmail())
                .thenReturn("test@gmail.com");

        PendingRegistration pending =
                PendingRegistration.builder()
                        .email("test@gmail.com")
                        .emailOtpHash("encoded-otp")
                        .otpExpiresAt(
                                LocalDateTime.now().minusMinutes(1)
                        )
                        .build();

        when(pendingRegistrationService.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(pending));

        assertThrows(
                BadCredentialsException.class,
                () -> authService.verifyRegistrationOtp(request)
        );

        verify(pendingRegistrationService)
                .delete(pending);

        verify(candidateRepository, never())
                .save(any());
    }


    @Test
    void verifyRegistrationOtp_shouldThrowException_whenOtpInvalid() {

        VerifyOtpRequestDto request =
                mock(VerifyOtpRequestDto.class);

        when(request.getEmail())
                .thenReturn("test@gmail.com");

        when(request.getEmailOtp())
                .thenReturn("999999");

        PendingRegistration pending =
                PendingRegistration.builder()
                        .email("test@gmail.com")
                        .emailOtpHash("encoded-otp")
                        .otpExpiresAt(
                                LocalDateTime.now().plusMinutes(5)
                        )
                        .build();

        when(pendingRegistrationService.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(pending));

        when(passwordEncoder.matches(
                "999999",
                "encoded-otp"
        )).thenReturn(false);

        assertThrows(
                BadCredentialsException.class,
                () -> authService.verifyRegistrationOtp(request)
        );

        verify(candidateRepository, never())
                .save(any());
    }


    //login
    @Test
    void login_shouldReturnTokenSuccessfully() {

        LoginRequestDto dto =
                mock(LoginRequestDto.class);

        when(dto.getEmail())
                .thenReturn("test@gmail.com");

        when(dto.getPassword())
                .thenReturn("password");

        Candidate candidate =
                Candidate.builder()
                        .id(10L)
                        .username("TestUser")
                        .email("test@gmail.com")
                        .password("encoded-password")
                        .role(Role.CANDIDATE)
                        .emailVerified(true)
                        .mobileVerified(true)
                        .enabled(true)
                        .build();

        when(candidateRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(candidate));

        when(authenticationManager.authenticate(any()))
                .thenReturn(
                        new UsernamePasswordAuthenticationToken(
                                "test@gmail.com",
                                null
                        )
                );

        when(jwtService.generateToken(candidate))
                .thenReturn("jwt-token");

        LoginResponseDto response =
                authService.login(dto);

        assertNotNull(response);

        assertEquals(
                "jwt-token",
                response.getAccessToken()
        );

        assertEquals(
                "Bearer",
                response.getTokenType()
        );

        assertEquals(
                10L,
                response.getUserId()
        );

        assertEquals(
                "TestUser",
                response.getUsername()
        );

        assertEquals(
                "test@gmail.com",
                response.getEmail()
        );

        assertEquals(
                Role.CANDIDATE,
                response.getRole()
        );

        verify(authenticationManager)
                .authenticate(any());

        verify(jwtService)
                .generateToken(candidate);
    }


    @Test
    void login_shouldThrowException_whenCandidateNotFound() {

        LoginRequestDto dto =
                mock(LoginRequestDto.class);

        when(dto.getEmail())
                .thenReturn("missing@gmail.com");

        when(candidateRepository.findByEmail("missing@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(dto)
        );

        verify(authenticationManager, never())
                .authenticate(any());
    }


    @Test
    void login_shouldThrowException_whenCandidateDisabled() {

        LoginRequestDto dto =
                mock(LoginRequestDto.class);

        when(dto.getEmail())
                .thenReturn("test@gmail.com");

        Candidate candidate =
                Candidate.builder()
                        .id(10L)
                        .email("test@gmail.com")
                        .enabled(false)
                        .emailVerified(true)
                        .mobileVerified(true)
                        .build();

        when(candidateRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(candidate));

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(dto)
        );

        verify(authenticationManager, never())
                .authenticate(any());
    }


    @Test
    void login_shouldThrowException_whenEmailNotVerified() {

        LoginRequestDto dto =
                mock(LoginRequestDto.class);

        when(dto.getEmail())
                .thenReturn("test@gmail.com");

        Candidate candidate =
                Candidate.builder()
                        .id(10L)
                        .email("test@gmail.com")
                        .enabled(true)
                        .emailVerified(false)
                        .mobileVerified(true)
                        .build();

        when(candidateRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(candidate));

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(dto)
        );
    }


    @Test
    void login_shouldThrowException_whenMobileNotVerified() {

        LoginRequestDto dto =
                mock(LoginRequestDto.class);

        when(dto.getEmail())
                .thenReturn("test@gmail.com");

        Candidate candidate =
                Candidate.builder()
                        .id(10L)
                        .email("test@gmail.com")
                        .enabled(true)
                        .emailVerified(true)
                        .mobileVerified(false)
                        .build();

        when(candidateRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(candidate));

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(dto)
        );
    }

//    //get current user
//    @Test
//    void getCurrentUser_shouldReturnCurrentUserSuccessfully() {
//
//        Candidate candidate =
//                Candidate.builder()
//                        .id(12L)
//                        .username("SkillUser")
//                        .email("skill@test.com")
//                        .role(Role.CANDIDATE)
//                        .build();
//
//        UserPrincipal principal =
//                UserPrincipal.builder()
//                        .userId(12L)
//                        .email("skill@test.com")
//                        .build();
//
//        SecurityContextHolder
//                .getContext()
//                .setAuthentication(
//                        new UsernamePasswordAuthenticationToken(
//                                principal,
//                                null));
//
//        when(candidateRepository.findById(12L))
//                .thenReturn(Optional.of(candidate));
//
//        CurrentUserDto response =
//                authService.getCurrentUser();
//
//        assertNotNull(response);
//
//        assertEquals(
//                12L,
//                response.getUserId());
//
//        assertEquals(
//                "SkillUser",
//                response.getUsername());
//
//        assertEquals(
//                "skill@test.com",
//                response.getEmail());
//
//        assertEquals(
//                Role.CANDIDATE,
//                response.getRole());
//    }


    @Test
    void getCurrentUser_shouldThrowException_whenAuthenticationMissing() {

        SecurityContextHolder.clearContext();

        assertThrows(
                BadCredentialsException.class,
                () -> authService.getCurrentUser());
    }

//
//    @Test
//    void getCurrentUser_shouldThrowException_whenPrincipalIsInvalid() {
//
//        SecurityContextHolder
//                .getContext()
//                .setAuthentication(
//                        new UsernamePasswordAuthenticationToken(
//                                "wrong-principal",
//                                null));
//
//        assertThrows(
//                UsernameNotFoundException.class,
//                () -> authService.getCurrentUser());
//    }
//
//
//    @Test
//    void getCurrentUser_shouldThrowException_whenCandidateNotFound() {
//
//        UserPrincipal principal =
//                UserPrincipal.builder()
//                        .userId(999L)
//                        .email("missing@test.com")
//                        .build();
//
//        SecurityContextHolder
//                .getContext()
//                .setAuthentication(
//                        new UsernamePasswordAuthenticationToken(
//                                principal, null));
//
//        when(candidateRepository.findById(999L))
//                .thenReturn(Optional.empty());
//
//        assertThrows(
//                UsernameNotFoundException.class,
//                () -> authService.getCurrentUser()
//        );
//    }


    //admin login
    @Test
    void adminLogin_shouldReturnTokenSuccessfully() {

        AdminLoginRequestDto request =
                mock(AdminLoginRequestDto.class);

        when(request.getEmail())
                .thenReturn("admin@test.com");

        when(request.getPassword())
                .thenReturn("password");

        Admin admin =
                Admin.builder()
                        .id(1L)
                        .email("admin@test.com")
                        .role(Role.ADMIN)
                        .build();

        when(adminRepository.findByEmail("admin@test.com"))
                .thenReturn(Optional.of(admin));

        when(authenticationManager.authenticate(any()))
                .thenReturn(
                        new UsernamePasswordAuthenticationToken(
                                "admin@test.com",
                                null));

        when(jwtService.generateToken(admin))
                .thenReturn("admin-jwt-token");

        HrLoginResponseDto response =
                authService.adminLogin(request);

        assertNotNull(response);

        assertEquals(
                "admin-jwt-token",
                response.getAccessToken());

        assertEquals(
                "Bearer",
                response.getTokenType());

        assertEquals(
                "admin@test.com",
                response.getEmail());

        assertEquals(
                Role.ADMIN,
                response.getRole());

        verify(authenticationManager)
                .authenticate(any());

        verify(jwtService)
                .generateToken(admin);
    }


    @Test
    void adminLogin_shouldThrowException_whenAdminNotFound() {

        AdminLoginRequestDto request =
                mock(AdminLoginRequestDto.class);

        when(request.getEmail())
                .thenReturn("missing@test.com");

        when(adminRepository.findByEmail("missing@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> authService.adminLogin(request)
        );
    }


    @Test
    void adminLogin_shouldThrowException_whenUserIsNotAdmin() {

        AdminLoginRequestDto request =
                mock(AdminLoginRequestDto.class);

        when(request.getEmail())
                .thenReturn("user@test.com");

        Admin admin =
                Admin.builder()
                        .id(2L)
                        .email("user@test.com")
                        .role(Role.CANDIDATE)
                        .build();

        when(adminRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(admin));

        assertThrows(
                BadCredentialsException.class,
                () -> authService.adminLogin(request)
        );

        verify(authenticationManager, never())
                .authenticate(any());
    }


    //admin logout
    @Test
    void adminLogout_shouldBlacklistToken() {

        String token = "admin-jwt-token";

        authService.adminLogout(token);

        verify(tokenBlacklist)
                .blacklistToken(token);

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication());
    }


    @Test
    void adminLogout_shouldThrowException_whenTokenNull() {

        assertThrows(
                IllegalArgumentException.class,
                () -> authService.adminLogout(null));

        verify(tokenBlacklist, never())
                .blacklistToken(anyString());
    }


    @Test
    void adminLogout_shouldThrowException_whenTokenBlank() {

        assertThrows(
                IllegalArgumentException.class,
                () -> authService.adminLogout("   ")
        );

        verify(tokenBlacklist, never())
                .blacklistToken(anyString());
    }

    //user logout
    @Test
    void logout_shouldBlacklistToken() {

        String token = "user-jwt-token";

        authService.logout(token);

        verify(tokenBlacklist)
                .blacklistToken(token);

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication());
    }


    @Test
    void logout_shouldThrowException_whenTokenNull() {

        assertThrows(
                IllegalArgumentException.class,
                () -> authService.logout(null));

        verify(tokenBlacklist, never())
                .blacklistToken(anyString());
    }


    @Test
    void logout_shouldThrowException_whenTokenBlank() {

        assertThrows(
                IllegalArgumentException.class,
                () -> authService.logout(""));

        verify(tokenBlacklist, never())
                .blacklistToken(anyString());
    }


    //initate password
    @Test
    void initiatePasswordReset_shouldSendOtpSuccessfully() {

        String email = "test@gmail.com";

        Candidate candidate =
                Candidate.builder()
                        .id(10L)
                        .email(email)
                        .enabled(true)
                        .emailVerified(true)
                        .build();

        when(candidateRepository.findByEmail(email))
                .thenReturn(Optional.of(candidate));

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encoded-otp");

        String response = authService.initiatePasswordReset(email);

        assertEquals("OTP sent to email.", response);

        assertEquals("encoded-otp", candidate.getEmailOtpHash());

        assertNotNull(candidate.getOtpExpiresAt());

        verify(candidateRepository).save(candidate);

        verify(emailService).sendOtp(eq(email), anyString());
    }


    @Test
    void initiatePasswordReset_shouldThrowException_whenCandidateNotFound() {

        String email = "missing@gmail.com";

        when(candidateRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> authService.initiatePasswordReset(email));

        verify(emailService, never())
                .sendOtp(anyString(), anyString());
    }


    @Test
    void initiatePasswordReset_shouldThrowException_whenCandidateDisabled() {

        String email = "test@gmail.com";

        Candidate candidate =
                Candidate.builder()
                        .id(10L)
                        .email(email)
                        .enabled(false)
                        .emailVerified(true)
                        .build();

        when(candidateRepository.findByEmail(email))
                .thenReturn(Optional.of(candidate));

        assertThrows(
                BadCredentialsException.class,
                () -> authService.initiatePasswordReset(email));

        verify(candidateRepository, never())
                .save(any());

        verify(emailService, never())
                .sendOtp(anyString(), anyString());
    }


    @Test
    void initiatePasswordReset_shouldThrowException_whenEmailNotVerified() {

        String email = "test@gmail.com";

        Candidate candidate =
                Candidate.builder()
                        .id(10L)
                        .email(email)
                        .enabled(true)
                        .emailVerified(false)
                        .build();

        when(candidateRepository.findByEmail(email))
                .thenReturn(Optional.of(candidate));

        assertThrows(
                BadCredentialsException.class,
                () -> authService.initiatePasswordReset(email));

        verify(candidateRepository, never())
                .save(any());
    }


    //reset password
    @Test
    void resetPassword_shouldResetPasswordSuccessfully() {

        ResetPasswordRequestDto request =
                mock(ResetPasswordRequestDto.class);
        when(request.getEmail()).thenReturn("test@gmail.com");

        when(request.getEmailOtp()).thenReturn("123456");

        when(request.getNewPassword()).thenReturn("newPassword");

        Candidate candidate =
                Candidate.builder()
                        .id(10L)
                        .email("test@gmail.com")
                        .emailOtpHash("encoded-otp")
                        .otpExpiresAt(
                                LocalDateTime.now().plusMinutes(5))
                        .build();

        when(candidateRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(candidate));

        when(passwordEncoder.matches(
                "123456",
                "encoded-otp"
        )).thenReturn(true);

        when(passwordEncoder.encode("newPassword")).thenReturn("encoded-new-password");

        String response = authService.resetPassword(request);

        assertEquals("Password has been reset successfully.", response);

        assertEquals("encoded-new-password", candidate.getPassword());

        assertNull(candidate.getEmailOtpHash());

        assertNull(candidate.getOtpExpiresAt());

        verify(candidateRepository).save(candidate);
    }


    @Test
    void resetPassword_shouldThrowException_whenCandidateNotFound() {

        ResetPasswordRequestDto request =
                mock(ResetPasswordRequestDto.class);

        when(request.getEmail())
                .thenReturn("missing@gmail.com");

        when(candidateRepository.findByEmail("missing@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> authService.resetPassword(request)
        );
    }


    @Test
    void resetPassword_shouldThrowException_whenOtpExpired() {

        ResetPasswordRequestDto request =
                mock(ResetPasswordRequestDto.class);

        when(request.getEmail())
                .thenReturn("test@gmail.com");

        Candidate candidate =
                Candidate.builder()
                        .email("test@gmail.com")
                        .emailOtpHash("encoded-otp")
                        .otpExpiresAt(
                                LocalDateTime.now().minusMinutes(1)
                        )
                        .build();

        when(candidateRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(candidate));

        assertThrows(
                BadCredentialsException.class,
                () -> authService.resetPassword(request)
        );

        verify(candidateRepository, never())
                .save(any());
    }


    @Test
    void resetPassword_shouldThrowException_whenOtpInvalid() {

        ResetPasswordRequestDto request =
                mock(ResetPasswordRequestDto.class);

        when(request.getEmail())
                .thenReturn("test@gmail.com");

        when(request.getEmailOtp())
                .thenReturn("999999");

        Candidate candidate =
                Candidate.builder()
                        .email("test@gmail.com")
                        .emailOtpHash("encoded-otp")
                        .otpExpiresAt(
                                LocalDateTime.now().plusMinutes(5)
                        )
                        .build();

        when(candidateRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(candidate));

        when(passwordEncoder.matches(
                "999999",
                "encoded-otp"
        )).thenReturn(false);

        assertThrows(
                BadCredentialsException.class,
                () -> authService.resetPassword(request));

        verify(candidateRepository, never())
                .save(any());
    }
}
