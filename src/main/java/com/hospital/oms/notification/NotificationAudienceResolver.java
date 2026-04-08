package com.hospital.oms.notification;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationAudienceResolver {

    public List<NotificationRole> rolesForEvent(String event) {
        return switch (event) {
            case "SUBMITTED" ->
                    List.of(
                            NotificationRole.PATIENT,
                            NotificationRole.ORDERING_CLINICIAN,
                            NotificationRole.FULFILMENT_QUEUE);
            case "CLAIMED", "COMPLETED" ->
                    List.of(
                            NotificationRole.PATIENT,
                            NotificationRole.ORDERING_CLINICIAN,
                            NotificationRole.ASSIGNED_STAFF);
            case "CANCELLED" ->
                    List.of(
                            NotificationRole.PATIENT,
                            NotificationRole.ORDERING_CLINICIAN,
                            NotificationRole.ADMINISTRATORS);
            default -> List.of(NotificationRole.ADMINISTRATORS);
        };
    }
}
