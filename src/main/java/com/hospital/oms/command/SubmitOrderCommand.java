package com.hospital.oms.command;

import com.hospital.oms.domain.OrderType;
import com.hospital.oms.domain.Priority;
import com.hospital.oms.manager.OrderManager;

public class SubmitOrderCommand implements OrderCommand {

    private final OrderType type;
    private final String patientName;
    private final String orderingClinician;
    private final String description;
    private final Priority priority;
    private final String actor;
    private String createdOrderId;

    public SubmitOrderCommand(
            OrderType type,
            String patientName,
            String orderingClinician,
            String description,
            Priority priority,
            String actor) {
        this.type = type;
        this.patientName = patientName;
        this.orderingClinician = orderingClinician;
        this.description = description;
        this.priority = priority;
        this.actor = actor;
    }

    @Override
    public void execute(OrderManager manager) {
        manager.submit(this);
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
        return actor;
    }

    public OrderType getType() {
        return type;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getOrderingClinician() {
        return orderingClinician;
    }

    public String getDescription() {
        return description;
    }

    public Priority getPriority() {
        return priority;
    }
}
