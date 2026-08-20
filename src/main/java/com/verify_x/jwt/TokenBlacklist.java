package com.verify_x.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
@RequiredArgsConstructor
public class TokenBlacklist {

    private final JwtService jwtService;
    public final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    public void blacklistToken(String token) {
        long expiryMillis;
        try {
            expiryMillis = jwtService.extractAllClaimsExpiry(token);
        } catch (Exception ex) {
            // Malformed token being blacklisted defensively — keep it for 1h.
            expiryMillis = System.currentTimeMillis() + 3_600_000L;
        }
        blacklist.put(token, expiryMillis);
    }

    public boolean isBlacklisted(String token) {
        return blacklist.containsKey(token);
    }

    @Scheduled(fixedRate = 15, timeUnit = java.util.concurrent.TimeUnit.MINUTES)
    void evictExpired() {
        long now = System.currentTimeMillis();
        blacklist.entrySet().removeIf(entry -> entry.getValue() < now);
    }
}
