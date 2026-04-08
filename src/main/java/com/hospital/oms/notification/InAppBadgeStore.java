package com.hospital.oms.notification;

import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class InAppBadgeStore {

    private final Map<NotificationRole, Integer> badges = new EnumMap<>(NotificationRole.class);

    public InAppBadgeStore() {
        for (NotificationRole role : NotificationRole.values()) {
            badges.put(role, 0);
        }
    }

    public synchronized void increment(NotificationRole role) {
        badges.merge(role, 1, Integer::sum);
    }

    public synchronized Map<NotificationRole, Integer> snapshot() {
        return new EnumMap<>(badges);
    }

    public synchronized void reset(NotificationRole role) {
        badges.put(role, 0);
    }
}
