package com.hospital.oms.notification;

import com.hospital.oms.domain.Order;
import org.springframework.stereotype.Service;

/**
 * Week 1: structured console logging for notifications (who / what / change).
 */
@Service
public final class ConsoleNotificationService implements NotificationService {

    @Override
    public void notify(Order order, String event) {
        String who = describeAudience(order, event);
        System.out.printf(
                "[NOTIFY] who=%s | orderId=%s | type=%s | patient=%s | change=%s | status=%s%n",
                who,
                order.getId(),
                order.getType(),
                order.getPatientName(),
                event,
                order.getStatus());
    }

    private static String describeAudience(Order order, String event) {
        return switch (event) {
            case "SUBMITTED" ->
                    "ordering clinician "
                            + order.getOrderingClinician()
                            + "; shared fulfilment queue";
            case "CLAIMED" ->
                    "ordering clinician "
                            + order.getOrderingClinician()
                            + "; assigned staff "
                            + order.getClaimedByStaffId();
            case "COMPLETED" ->
                    "ordering clinician " + order.getOrderingClinician() + "; patient care team";
            case "CANCELLED" -> "ordering clinician " + order.getOrderingClinician() + "; administrators";
            default -> "stakeholders";
        };
    }
}
