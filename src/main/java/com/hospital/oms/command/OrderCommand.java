package com.hospital.oms.command;

import com.hospital.oms.manager.OrderManager;

public interface OrderCommand {

    void execute(OrderManager manager);

    void undo(OrderManager manager);

    String getCommandType();

    String getOrderId();

    String getActor();
}
