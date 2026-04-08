package com.hospital.oms.notification;

import com.hospital.oms.domain.Order;

public interface NotificationChannel {

    NotificationChannelType type();

    void notify(NotificationRole role, Order order, String event);
}