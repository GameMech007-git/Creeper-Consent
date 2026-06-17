package com.anantaya.creeperconsent;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CreeperConsentState {

    private static final Set<UUID> PENDING = ConcurrentHashMap.newKeySet();
    private static final Set<UUID> APPROVED = ConcurrentHashMap.newKeySet();

    private CreeperConsentState() {
    }

    public static boolean markPending(UUID uuid) {
        return PENDING.add(uuid);
    }

    public static void clearPending(UUID uuid) {
        PENDING.remove(uuid);
    }

    public static void approveOnce(UUID uuid) {
        APPROVED.add(uuid);
        PENDING.remove(uuid);
    }

    public static boolean consumeApproval(UUID uuid) {
        return APPROVED.remove(uuid);
    }

    public static void clear(UUID uuid) {
        PENDING.remove(uuid);
        APPROVED.remove(uuid);
    }
}