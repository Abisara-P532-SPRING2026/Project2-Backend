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
                            NotificationRole.CLINICIAN,
                            NotificationRole.FULFILMENT_STAFF);
            case "CLAIMED", "COMPLETED" ->
                    List.of(
                            NotificationRole.PATIENT,
                            NotificationRole.CLINICIAN,
                            NotificationRole.FULFILMENT_STAFF);
            case "CANCELLED" ->
                    List.of(
                            NotificationRole.PATIENT,
                            NotificationRole.CLINICIAN,
                            NotificationRole.FULFILMENT_STAFF);
            default -> List.of(NotificationRole.CLINICIAN);
        };
    }
}
