package com.verify_x.repository;

import com.verify_x.entity.PendingRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PendingRegistrationRepository extends JpaRepository<PendingRegistration, Long> {

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<PendingRegistration> findByEmail(String email);

    void deleteByEmail(String email);

    long deleteByOtpExpiresAtBefore(LocalDateTime time);
}