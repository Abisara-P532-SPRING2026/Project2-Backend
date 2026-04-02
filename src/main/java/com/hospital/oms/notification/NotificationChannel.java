package com.hospital.oms.notification;

import com.hospital.oms.domain.Order;

public interface NotificationChannel {

    void notify(Order order, String event);
}
