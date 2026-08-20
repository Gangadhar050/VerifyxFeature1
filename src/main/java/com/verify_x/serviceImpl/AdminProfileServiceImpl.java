package com.verify_x.serviceImpl;

import com.verify_x.dto.AdminProfileResponse;
import com.verify_x.dto.AdminProfileUpdateRequest;
import com.verify_x.entity.Admin;
import com.verify_x.jwt.UserPrincipal;
import com.verify_x.repository.AdminRepository;
import com.verify_x.services.AdminProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminProfileServiceImpl implements AdminProfileService {

    private final AdminRepository adminRepository;


    @Override
    public AdminProfileResponse getProfile() {

        Admin admin = getLoggedInAdmin();

        return mapToResponse(admin);
    }


    @Override
    public AdminProfileResponse updateProfile(
            AdminProfileUpdateRequest request,
            MultipartFile profilePhoto) {

        Admin admin = getLoggedInAdmin();


        if (request.getFullName() != null) {
            admin.setFullName(request.getFullName());
        }

        if (request.getMobileNumber() != null) {
            admin.setMobileNumber(request.getMobileNumber());
        }

        if (request.getLocation() != null) {
            admin.setLocation(request.getLocation());
        }


        if (request.getDesignation() != null) {
            admin.setDesignation(request.getDesignation());
        }

        if (request.getCompany() != null) {
            admin.setCompany(request.getCompany());
        }

        if (request.getEmployeeId() != null) {
            admin.setEmployeeId(request.getEmployeeId());
        }

        if (request.getDepartment() != null) {
            admin.setDepartment(request.getDepartment());
        }

        if (request.getReportingManager() != null) {
            admin.setReportingManager(request.getReportingManager());
        }

        if (request.getWorkMode() != null) {
            admin.setWorkMode(request.getWorkMode());
        }

        if (request.getJoiningDate() != null) {
            admin.setJoiningDate(request.getJoiningDate());
        }


        // =========================
        // PROFILE PHOTO
        // =========================

        if (profilePhoto != null && !profilePhoto.isEmpty()) {

            try {
                admin.setProfilePhoto(profilePhoto.getBytes());

            } catch (IOException e) {
                throw new RuntimeException(
                        "Failed to upload profile photo", e
                );
            }
        }


        adminRepository.save(admin);

        return mapToResponse(admin);
    }

    private Admin getLoggedInAdmin() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();

        String email = null;

        if (principal instanceof UserPrincipal userPrincipal) {
            email = userPrincipal.getEmail();
        } else if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        } else if (principal instanceof String principalString) {
            email = principalString;
        }

        if (email == null || email.isBlank()) {
            throw new RuntimeException(
                    "Unable to determine authenticated user's email. Principal: "
                            + principal
            );
        }

        final String authenticatedEmail = email;

        return adminRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Admin not found for email: " + authenticatedEmail
                        )
                );
    }

    private AdminProfileResponse mapToResponse(Admin admin) {

        return AdminProfileResponse.builder()

                .id(admin.getId())
                .email(admin.getEmail())

                .fullName(admin.getFullName())
                .mobileNumber(admin.getMobileNumber())
                .location(admin.getLocation())

                .designation(admin.getDesignation())
                .company(admin.getCompany())
                .employeeId(admin.getEmployeeId())
                .department(admin.getDepartment())
                .reportingManager(admin.getReportingManager())
                .workMode(admin.getWorkMode())
                .joiningDate(admin.getJoiningDate())

                .active(admin.getActive())
                .lastLogin(admin.getLastLogin())

                .createdAt(admin.getCreatedAt())
                .updatedAt(admin.getUpdatedAt())

                .build();
    }
}