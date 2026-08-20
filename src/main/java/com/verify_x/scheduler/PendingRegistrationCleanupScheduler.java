package com.verify_x.scheduler;

import com.verify_x.services.PendingRegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PendingRegistrationCleanupScheduler {

    private final PendingRegistrationService pendingRegistrationService;

    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void cleanupExpiredPendingRegistrations() {
        long deleted = pendingRegistrationService.deleteExpired();

        if (deleted > 0) {
            log.info("Deleted {} expired pending registration(s).", deleted);
        }
    }
}