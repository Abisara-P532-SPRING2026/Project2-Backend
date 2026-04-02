package com.hospital.oms.handler;

import com.hospital.oms.domain.Order;

public class OrderProcessingContext {

    private final Order order;

    public OrderProcessingContext(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }
}
