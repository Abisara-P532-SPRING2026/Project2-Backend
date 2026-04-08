package com.hospital.oms.notification;

import com.hospital.oms.domain.Order;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Primary
public final class CompositeNotificationService implements NotificationService {

    private final Map<NotificationChannelType, NotificationChannel> channelsByType;
    private final NotificationAudienceResolver audienceResolver;
    private final InMemoryNotificationPreferences notificationPreferences;

    public CompositeNotificationService(
            List<NotificationChannel> channels,
            NotificationAudienceResolver audienceResolver,
            InMemoryNotificationPreferences notificationPreferences) {
        this.channelsByType = channels.stream().collect(Collectors.toMap(NotificationChannel::type, Function.identity()));
        this.audienceResolver = audienceResolver;
        this.notificationPreferences = notificationPreferences;
    }

    @Override
    public void notify(Order order, String event) {
        for (NotificationRole role : audienceResolver.rolesForEvent(event)) {
            for (NotificationChannelType channelType : notificationPreferences.getEnabledChannels(role)) {
                NotificationChannel channel = channelsByType.get(channelType);
                if (channel != null) {
                    channel.notify(role, order, event);
                }
            }
        }
    }
}
