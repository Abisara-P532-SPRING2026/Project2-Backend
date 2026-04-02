package com.hospital.oms.notification;

import com.hospital.oms.domain.Order;

public interface NotificationService {

    void notify(Order order, String event);
}
