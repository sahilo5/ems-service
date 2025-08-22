package com.SadhyaSiddhi.ems_service.services;

import com.SadhyaSiddhi.ems_service.exceptions.TooFrequentScanException;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class QrReplayGuard {
    private final ConcurrentHashMap<String, Long> seen = new ConcurrentHashMap<>();
    private final long ttlMillis = 20_000; // 20s

    public void assertNotReused(Long userId, long slot) {
        String key = userId + ":" + slot;
        long now = System.currentTimeMillis();
        Long prev = seen.putIfAbsent(key, now);
        if (prev != null && (now - prev) < ttlMillis) {
            throw new TooFrequentScanException("Token already used in this window");
        }
        // Opportunistic cleanup
        if (seen.size() > 10_000) {
            seen.entrySet().removeIf(e -> now - e.getValue() > ttlMillis);
        }
    }
}
