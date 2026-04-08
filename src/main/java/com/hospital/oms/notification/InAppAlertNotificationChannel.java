package com.hospital.oms.notification;

import com.hospital.oms.domain.Order;
import org.springframework.stereotype.Component;

@Component
public class InAppAlertNotificationChannel implements NotificationChannel {

    private final InAppBadgeStore badgeStore;

    public InAppAlertNotificationChannel(InAppBadgeStore badgeStore) {
        this.badgeStore = badgeStore;
    }

    @Override
    public NotificationChannelType type() {
        return NotificationChannelType.IN_APP;
    }

    @Override
    public void notify(NotificationRole role, Order order, String event) {
        badgeStore.increment(role);
    }
}
