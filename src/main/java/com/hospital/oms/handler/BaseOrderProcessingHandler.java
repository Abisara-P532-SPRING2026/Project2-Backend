package com.hospital.oms.handler;

import com.hospital.oms.engine.OrderStorageEngine;


public class BaseOrderProcessingHandler implements OrderProcessingHandler {

    private final OrderStorageEngine orderStorageEngine;

    public BaseOrderProcessingHandler(OrderStorageEngine orderStorageEngine) {
        this.orderStorageEngine = orderStorageEngine;
    }

    @Override
    public void handleSubmit(OrderProcessingContext context) {
        orderStorageEngine.saveOrder(context.getOrder());
    }
}
