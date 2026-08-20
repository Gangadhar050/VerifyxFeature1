package com.verify_x.services;

import com.verify_x.entity.PendingRegistration;

import java.util.Optional;

public interface PendingRegistrationService {

    PendingRegistration save(PendingRegistration pendingRegistration);

    Optional<PendingRegistration> findByEmail(String email);

    void delete(PendingRegistration pendingRegistration);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    long deleteExpired();
}