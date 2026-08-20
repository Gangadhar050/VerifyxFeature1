package com.verify_x.serviceImpl;

import com.verify_x.entity.PendingRegistration;
import com.verify_x.repository.PendingRegistrationRepository;
import com.verify_x.services.PendingRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PendingRegistrationServiceImpl implements PendingRegistrationService {

    private final PendingRegistrationRepository pendingRegistrationRepository;

    @Override
    public PendingRegistration save(PendingRegistration pendingRegistration) {
        return pendingRegistrationRepository.save(pendingRegistration);
    }

    @Override
    public Optional<PendingRegistration> findByEmail(String email) {
        return pendingRegistrationRepository.findByEmail(email);
    }

    @Override
    public void delete(PendingRegistration pendingRegistration) {
        pendingRegistrationRepository.delete(pendingRegistration);
    }

    @Override
    public boolean existsByEmail(String email) {
        return pendingRegistrationRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return pendingRegistrationRepository.existsByPhoneNumber(phoneNumber);
    }
    @Override
    public long deleteExpired() {
        return pendingRegistrationRepository.deleteByOtpExpiresAtBefore(LocalDateTime.now());
    }
}