package com.votrebanque.infrastructure.adapters.inbound.rest.security;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * In-memory, single-instance rate limiter. Sufficient for a single-node deployment;
 * would need a shared store (e.g. Redis) behind multiple backend instances.
 */
@Component
public class DemoSessionRateLimiter {

    private final ConcurrentHashMap<String, Instant> lastRequestByIp = new ConcurrentHashMap<>();
    private final Duration minInterval;

    public DemoSessionRateLimiter(@Value("${app.demo.session.min-interval-seconds:120}") long minIntervalSeconds) {
        this.minInterval = Duration.ofSeconds(minIntervalSeconds);
    }

    public boolean tryAcquire(String clientIp) {
        Instant now = Instant.now();
        Instant previous = lastRequestByIp.merge(clientIp, now, (oldValue, newValue) ->
            now.isBefore(oldValue.plus(minInterval)) ? oldValue : now
        );
        return previous.equals(now);
    }
}
