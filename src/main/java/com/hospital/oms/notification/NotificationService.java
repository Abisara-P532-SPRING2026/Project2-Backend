package com.hospital.oms.notification;

import com.hospital.oms.domain.Order;

/**
 * Utility / cross-cutting: notification channel for status changes.
 */
public interface NotificationService {

    void notify(Order order, String event);
}
