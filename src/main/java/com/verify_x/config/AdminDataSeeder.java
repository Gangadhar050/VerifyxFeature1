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

        if (adminRepository.existsByEmail("admin@verifyx.com")) {
            return;
        }

        Admin admin = Admin.builder()
                .email("admin@verifyx.com")
                .password(passwordEncoder.encode("Admin@123"))
                .role(Role.ADMIN)
                .build();

        adminRepository.save(admin);

        log.info("Default Admin Created");
    }
}