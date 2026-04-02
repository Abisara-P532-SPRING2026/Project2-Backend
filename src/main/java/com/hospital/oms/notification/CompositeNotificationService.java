package com.hospital.oms.notification;

import com.hospital.oms.domain.Order;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public final class CompositeNotificationService implements NotificationService {

    private final List<NotificationChannel> channels;

    public CompositeNotificationService(List<NotificationChannel> channels) {
        this.channels = List.copyOf(channels);
    }

    @Override
    public void notify(Order order, String event) {
        for (NotificationChannel channel : channels) {
            channel.notify(order, event);
        }
    }
}
