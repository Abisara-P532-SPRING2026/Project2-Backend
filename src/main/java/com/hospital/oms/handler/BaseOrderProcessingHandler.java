package com.hospital.oms.handler;

import com.hospital.oms.resourceaccess.OrderAccess;

/**
 * Innermost handler: persists the order without knowing about outer decorators.
 */
public class BaseOrderProcessingHandler implements OrderProcessingHandler {

    private final OrderAccess orderAccess;

    public BaseOrderProcessingHandler(OrderAccess orderAccess) {
        this.orderAccess = orderAccess;
    }

    @Override
    public void handleSubmit(OrderProcessingContext context) {
        orderAccess.saveOrder(context.getOrder());
    }
}
