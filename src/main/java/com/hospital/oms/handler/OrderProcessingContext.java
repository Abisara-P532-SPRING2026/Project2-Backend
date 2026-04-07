package com.hospital.oms.handler;

import com.hospital.oms.domain.Order;
import com.hospital.oms.engine.OrderStorageEngine;

public class OrderProcessingContext {

    private final Order order;
    private final OrderStorageEngine orderStorageEngine;

    public OrderProcessingContext(Order order, OrderStorageEngine orderStorageEngine) {
        this.order = order;
        this.orderStorageEngine = orderStorageEngine;
    }

    public Order getOrder() {
        return order;
    }

    public OrderStorageEngine getOrderStorageEngine() {
        return orderStorageEngine;
    }
}
