package com.hospital.oms.web;

import com.hospital.oms.notification.InAppBadgeStore;
import com.hospital.oms.notification.InMemoryNotificationPreferences;
import com.hospital.oms.notification.NotificationChannelType;
import com.hospital.oms.notification.NotificationRole;
import com.hospital.oms.web.dto.NotificationPreferenceUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/settings")
public class NotificationSettingsController {

    private final InMemoryNotificationPreferences notificationPreferences;
    private final InAppBadgeStore badgeStore;

    public NotificationSettingsController(
            InMemoryNotificationPreferences notificationPreferences, InAppBadgeStore badgeStore) {
        this.notificationPreferences = notificationPreferences;
        this.badgeStore = badgeStore;
    }

    @GetMapping("/notification-preferences")
    public Map<String, Set<NotificationChannelType>> getNotificationPreferences() {
        return notificationPreferences.snapshot().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().name(), Map.Entry::getValue));
    }

    @PostMapping("/notification-preferences/{role}")
    public Map<String, Set<NotificationChannelType>> updateNotificationPreferences(
            @PathVariable NotificationRole role, @Valid @RequestBody NotificationPreferenceUpdateRequest request) {
        notificationPreferences.setEnabledChannels(role, request.enabledChannels());
        return Map.of(role.name(), notificationPreferences.getEnabledChannels(role));
    }

    @GetMapping("/in-app-badges")
    public Map<String, Integer> getInAppBadges() {
        return badgeStore.snapshot().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().name(), Map.Entry::getValue));
    }

    @PostMapping("/in-app-badges/{role}/reset")
    public Map<String, Integer> resetInAppBadge(@PathVariable NotificationRole role) {
        badgeStore.reset(role);
        return Map.of(role.name(), badgeStore.snapshot().getOrDefault(role, 0));
    }
}
