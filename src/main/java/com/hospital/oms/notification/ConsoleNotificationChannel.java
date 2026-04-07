package com.hospital.oms.notification;

import com.hospital.oms.domain.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class ConsoleNotificationChannel implements NotificationChannel {

    @Override
    public void notify(Order order, String event) {
        for (String party : partiesFor(event)) {
            printToParty(party, order, event);
        }
    }

    private static List<String> partiesFor(String event) {
        return switch (event) {
            case "SUBMITTED" -> List.of("patient", "ordering clinician", "fulfilment queue");
            case "CLAIMED" -> List.of("patient", "ordering clinician", "assigned staff");
            case "COMPLETED" -> List.of("patient", "ordering clinician", "assigned staff");
            case "CANCELLED" -> List.of("patient", "ordering clinician", "administrators");
            default -> List.of("stakeholders");
        };
    }

    private static void printToParty(String party, Order order, String event) {
        String message = messageForParty(party, order, event);
        System.out.printf(
                "[NOTIFY → %s] %s | orderId=%s | type=%s | patient=%s | change=%s | status=%s%n",
                party,
                message,
                order.getId(),
                order.getType(),
                order.getPatientName(),
                event,
                order.getStatus());
    }

    private static String clinicianLabel(Order order) {
        return order.getOrderingClinicianName() + " [id=" + order.getOrderingClinicianId() + "]";
    }

    private static String messageForParty(String party, Order order, String event) {
        String patient = order.getPatientName();
        return switch (party) {
            case "patient" -> patientMessage(patient, event, order);
            case "ordering clinician" -> "To " + clinicianLabel(order) + ": " + clinicianMessage(event, order);
            case "fulfilment queue" -> fulfilmentQueueMessage(order, event);
            case "assigned staff" -> staffMessage(order, event);
            case "administrators" -> "Admin: order cancelled for patient " + patient + " — retain audit trail.";
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

    private static String fulfilmentQueueMessage(Order order, String event) {
        if ("SUBMITTED".equals(event)) {
            return "Fulfilment queue: new "
                    + order.getType()
                    + " order — patient "
                    + order.getPatientName()
                    + ", priority "
                    + order.getPriority()
                    + ", ordered by "
                    + clinicianLabel(order)
                    + " — available to claim.";
        }
        return "Fulfilment: " + event + " | patient=" + order.getPatientName();
    }

    private static String staffMessage(Order order, String event) {
        String sid = order.getClaimedByStaffId();
        return switch (event) {
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
