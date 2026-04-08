package com.hospital.oms.web.dto;

import com.hospital.oms.notification.NotificationChannelType;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record NotificationPreferenceUpdateRequest(@NotNull Set<NotificationChannelType> enabledChannels) {}
