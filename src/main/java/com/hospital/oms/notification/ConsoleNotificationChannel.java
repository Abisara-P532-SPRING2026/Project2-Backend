package com.hospital.oms.notification;

import com.hospital.oms.domain.Order;
import org.springframework.stereotype.Component;


@Component
public final class ConsoleNotificationChannel implements NotificationChannel {

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

    private static String clinicianLabel(Order order) {
        return order.getOrderingClinicianName() + " [id=" + order.getOrderingClinicianId() + "]";
    }

    private static String describeAudience(Order order, String event) {
        return switch (event) {
            case "SUBMITTED" -> "ordering clinician " + clinicianLabel(order) + "; shared fulfilment queue";
            case "CLAIMED" ->
                    "ordering clinician "
                            + clinicianLabel(order)
                            + "; assigned staff "
                            + order.getClaimedByStaffId();
            case "COMPLETED" -> "ordering clinician " + clinicianLabel(order) + "; patient care team";
            case "CANCELLED" -> "ordering clinician " + clinicianLabel(order) + "; administrators";
            default -> "stakeholders";
        };
    }
}
