package com.hospital.oms.command;

import com.hospital.oms.manager.OrderManager;

/**
 * Command pattern: encapsulates an order action for logging and future undo.
 */
public interface OrderCommand {

    void execute(OrderManager manager);

    String getCommandType();

    String getOrderId();

    String getActor();
}
