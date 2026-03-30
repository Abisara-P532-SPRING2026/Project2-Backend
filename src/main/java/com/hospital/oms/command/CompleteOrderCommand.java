package com.hospital.oms.command;

import com.hospital.oms.manager.OrderManager;

public class CompleteOrderCommand implements OrderCommand {

    private final String orderId;
    private final String staffId;

    public CompleteOrderCommand(String orderId, String staffId) {
        this.orderId = orderId;
        this.staffId = staffId;
    }

    @Override
    public void execute(OrderManager manager) {
        manager.complete(this);
    }

    @Override
    public String getCommandType() {
        return "COMPLETE";
    }

    @Override
    public String getOrderId() {
        return orderId;
    }

    @Override
    public String getActor() {
        return staffId;
    }

    public String getStaffId() {
        return staffId;
    }
}
