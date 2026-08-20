package com.verify_x.config;

import com.verify_x.entity.Admin;
import com.verify_x.enums.Role;
import com.verify_x.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminDataSeeder implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        String email = "admin@verifyx.com";
        String password = "Admin@123";

        Admin admin = adminRepository.findByEmail(email)
                .orElseGet(Admin::new);

        admin.setEmail(email);

        // IMPORTANT: always encode/reset the password
        admin.setPassword(passwordEncoder.encode(password));

        admin.setRole(Role.ADMIN);

        // Personal Information
        admin.setFullName("Verify-X HR Admin");
        admin.setMobileNumber(null);
        admin.setLocation(null);

        // Professional Information
        admin.setDesignation("HR Professional");
        admin.setCompany(null);
        admin.setEmployeeId(null);
        admin.setDepartment("Human Resources");
        admin.setReportingManager(null);
        admin.setWorkMode("Onsite");
        admin.setJoiningDate(null);

        // Account
        admin.setActive(true);
        admin.setLastLogin(null);

        adminRepository.save(admin);

        log.info("Default Admin created/updated: {}", email);
    }
}