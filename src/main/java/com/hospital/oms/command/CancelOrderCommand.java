package com.hospital.oms.command;

import com.hospital.oms.manager.OrderManager;

public class CancelOrderCommand implements OrderCommand {

    private final String orderId;
    private final String actor;

    public CancelOrderCommand(String orderId, String actor) {
        this.orderId = orderId;
        this.actor = actor;
    }

    @Override
    public void execute(OrderManager manager) {
        manager.cancel(this);
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
        return actor;
    }
}
