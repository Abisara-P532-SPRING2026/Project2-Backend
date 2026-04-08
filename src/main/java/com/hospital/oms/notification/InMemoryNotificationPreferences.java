package com.hospital.oms.notification;

import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Component
public class InMemoryNotificationPreferences {

    private final Map<NotificationRole, EnumSet<NotificationChannelType>> enabledByRole =
            new EnumMap<>(NotificationRole.class);

    public InMemoryNotificationPreferences() {
        for (NotificationRole role : NotificationRole.values()) {
            enabledByRole.put(role, EnumSet.of(NotificationChannelType.CONSOLE));
        }
    }

    public synchronized Set<NotificationChannelType> getEnabledChannels(NotificationRole role) {
        return Set.copyOf(enabledByRole.getOrDefault(role, EnumSet.of(NotificationChannelType.CONSOLE)));
    }

    public synchronized void setEnabledChannels(
            NotificationRole role, Set<NotificationChannelType> enabledChannels) {
        if (enabledChannels == null || enabledChannels.isEmpty()) {
            enabledByRole.put(role, EnumSet.noneOf(NotificationChannelType.class));
            return;
        }
        enabledByRole.put(role, EnumSet.copyOf(enabledChannels));
    }

    public synchronized Map<NotificationRole, Set<NotificationChannelType>> snapshot() {
        Map<NotificationRole, Set<NotificationChannelType>> out = new EnumMap<>(NotificationRole.class);
        for (NotificationRole role : NotificationRole.values()) {
            out.put(role, Set.copyOf(enabledByRole.getOrDefault(role, EnumSet.noneOf(NotificationChannelType.class))));
        }
        return out;
    }
}
