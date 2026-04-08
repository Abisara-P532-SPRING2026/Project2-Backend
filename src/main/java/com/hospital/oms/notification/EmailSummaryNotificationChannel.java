package com.hospital.oms.notification;

import com.hospital.oms.domain.Order;
import org.springframework.stereotype.Component;

@Component
public class EmailSummaryNotificationChannel implements NotificationChannel {

    @Override
    public NotificationChannelType type() {
        return NotificationChannelType.EMAIL;
    }

    @Override
    public void notify(NotificationRole role, Order order, String event) {
        System.out.printf(
                "[EMAIL_MOCK] {\"toRole\":\"%s\",\"subject\":\"OMS %s update\",\"orderId\":\"%s\",\"orderType\":\"%s\",\"patient\":\"%s\",\"event\":\"%s\",\"status\":\"%s\"}%n",
                role,
                event,
                order.getId(),
                order.getType(),
                order.getPatientName(),
                event,
                order.getStatus());
    }
}
