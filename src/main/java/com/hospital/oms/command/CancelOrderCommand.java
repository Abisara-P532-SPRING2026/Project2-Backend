package com.hospital.oms.command;

import com.hospital.oms.manager.OrderManager;

public class CancelOrderCommand implements OrderCommand {

    private final String orderId;
    private final String clinicianId;

    public CancelOrderCommand(String orderId, String clinicianId) {
        this.orderId = orderId;
        this.clinicianId = clinicianId;
    }

    @Override
    public void execute(OrderManager manager) {
        manager.cancel(this);
    }

    @Override
    public void undo(OrderManager manager) {
        
    }

    @Override
    public String getCommandType() {
        return "CANCEL";
    }

    @Override
    public String getOrderId() {
        return orderId;
    }

    @Override
    public String getActor() {
        return clinicianId;
    }

    public String getClinicianId() {
        return clinicianId;
    }
}
