package com.hospital.oms.command;

import com.hospital.oms.domain.OrderType;
import com.hospital.oms.domain.Priority;
import com.hospital.oms.manager.OrderManager;

public class SubmitOrderCommand implements OrderCommand {

    private final OrderType type;
    private final String patientName;
    private final String orderingClinicianId;
    private final String description;
    private final Priority priority;
    /** Clinician name for audit log / display. */
    private final String clinicianName;
    private String createdOrderId;

    public SubmitOrderCommand(
            OrderType type,
            String patientName,
            String orderingClinicianId,
            String description,
            Priority priority,
            String clinicianName) {
        this.type = type;
        this.patientName = patientName;
        this.orderingClinicianId = orderingClinicianId;
        this.description = description;
        this.priority = priority;
        this.clinicianName = clinicianName;
    }

    @Override
    public void execute(OrderManager manager) {
        manager.submit(this);
    }

    @Override
    public void undo(OrderManager manager) {
    }

    @Override
    public String getCommandType() {
        return "SUBMIT";
    }

    @Override
    public String getOrderId() {
        return createdOrderId == null ? "" : createdOrderId;
    }

    public void setCreatedOrderId(String createdOrderId) {
        this.createdOrderId = createdOrderId;
    }

    @Override
    public String getActor() {
        if (clinicianName != null && !clinicianName.isBlank()) {
            return clinicianName.trim();
        }
        return orderingClinicianId == null ? "" : orderingClinicianId.trim();
    }

    public OrderType getType() {
        return type;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getOrderingClinicianId() {
        return orderingClinicianId;
    }

    public String getClinicianName() {
        return clinicianName;
    }

    public String getDescription() {
        return description;
    }

    public Priority getPriority() {
        return priority;
    }
}
