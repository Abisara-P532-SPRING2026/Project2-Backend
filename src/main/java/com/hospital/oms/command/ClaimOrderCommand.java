package com.hospital.oms.command;

import com.hospital.oms.manager.OrderManager;

public class ClaimOrderCommand implements OrderCommand {

    private final String orderId;
    private final String staffId;

    public ClaimOrderCommand(String orderId, String staffId) {
        this.orderId = orderId;
        this.staffId = staffId;
    }

    @Override
    public void execute(OrderManager manager) {
        manager.claim(this);
    }

    @Override
    public String getCommandType() {
        return "CLAIM";
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
