package com.coffeelab.backend.service;

import com.coffeelab.backend.exception.BusinessException;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;

@Component
public class PersistenceGuard {
    private static final long FALLBACK_COOLDOWN_MILLIS = 30_000L;
    private volatile long fallbackUntil = 0L;

    public <T> T read(Supplier<T> databaseCall, Supplier<T> fallbackCall) {
        if (isFallbackActive()) {
            return fallbackCall.get();
        }
        try {
            return databaseCall.get();
        } catch (RuntimeException ex) {
            if (ex instanceof BusinessException) {
                throw ex;
            }
            activateFallback();
            return fallbackCall.get();
        }
    }

    public void write(Runnable databaseCall, Runnable fallbackCall) {
        if (isFallbackActive()) {
            fallbackCall.run();
            return;
        }
        try {
            databaseCall.run();
        } catch (RuntimeException ex) {
            if (ex instanceof BusinessException) {
                throw ex;
            }
            activateFallback();
            fallbackCall.run();
        }
    }

    private boolean isFallbackActive() {
        return System.currentTimeMillis() < fallbackUntil;
    }

    private void activateFallback() {
        fallbackUntil = System.currentTimeMillis() + FALLBACK_COOLDOWN_MILLIS;
    }
}
