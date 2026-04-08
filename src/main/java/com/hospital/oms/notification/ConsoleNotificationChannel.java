package com.hospital.oms.notification;

import com.hospital.oms.domain.Order;
import org.springframework.stereotype.Component;

@Component
public final class ConsoleNotificationChannel implements NotificationChannel {

    @Override
    public NotificationChannelType type() {
        return NotificationChannelType.CONSOLE;
    }

    @Override
    public void notify(NotificationRole role, Order order, String event) {
        printToRole(role, order, event);
    }

    private static void printToRole(NotificationRole role, Order order, String event) {
        String message = messageForRole(role, order, event);
        System.out.printf(
                "[NOTIFY → %s] %s | type=%s | patient=%s | change=%s | status=%s%n",
                role,
                message,
                order.getType(),
                order.getPatientName(),
                event,
                order.getStatus());
    }

    private static String clinicianLabel(Order order) {
        return order.getOrderingClinicianName() + " [id=" + order.getOrderingClinicianId() + "]";
    }

    private static String messageForRole(NotificationRole role, Order order, String event) {
        String patient = order.getPatientName();
        return switch (role) {
            case PATIENT -> patientMessage(patient, event, order);
            case CLINICIAN -> "To " + clinicianLabel(order) + ": " + clinicianMessage(event, order);
            case FULFILMENT_STAFF -> fulfilmentStaffMessage(order, event);
            default -> "Notice: " + event + " | order " + order.getId();
        };
    }

    private static String patientMessage(String patient, String event, Order order) {
        return switch (event) {
            case "SUBMITTED" ->
                    "Patient "
                            + patient
                            + ": a "
                            + order.getType()
                            + " order was placed for you; it is in the queue awaiting fulfilment.";
            case "CLAIMED" ->
                    "Patient "
                            + patient
                            + ": your order is now being handled by fulfilment staff (id="
                            + order.getClaimedByStaffId()
                            + ").";
            case "COMPLETED" ->
                    "Patient " + patient + ": your order is completed; follow any instructions from your care team.";
            case "CANCELLED" -> "Patient " + patient + ": your pending order was cancelled.";
            default -> "Patient " + patient + ": update on your order.";
        };
    }

    private static String clinicianMessage(String event, Order order) {
        return switch (event) {
            case "SUBMITTED" -> "your order was submitted; it is pending in the triaged queue.";
            case "CLAIMED" ->
                    "your order was claimed by staff id "
                            + order.getClaimedByStaffId()
                            + "; status is now in progress.";
            case "COMPLETED" -> "your order for patient " + order.getPatientName() + " was completed.";
            case "CANCELLED" -> "your pending order was cancelled.";
            default -> "update on your order.";
        };
    }

    private static String fulfilmentStaffMessage(Order order, String event) {
        String sid = order.getClaimedByStaffId();
        return switch (event) {
            case "SUBMITTED" ->
                    "Fulfilment staff: new "
                            + order.getType()
                            + " order for patient "
                            + order.getPatientName()
                            + " is available to claim.";
            case "CLAIMED" ->
                    "Staff "
                            + sid
                            + ": you claimed this order; complete it when work is finished.";
            case "COMPLETED" ->
                    "Staff "
                            + sid
                            + ": this order is marked completed (you were the assigned fulfiller).";
            default -> "Staff " + sid + ": " + event;
        };
    }
}
