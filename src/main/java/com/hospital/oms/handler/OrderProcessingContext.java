package com.hospital.oms.handler;

import com.hospital.oms.domain.Order;

/**
 * Context passed through the decorator chain for order submission.
 */
public class OrderProcessingContext {

    private final Order order;

    public OrderProcessingContext(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }
}
