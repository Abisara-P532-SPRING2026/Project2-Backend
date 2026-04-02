package com.hospital.oms.engine;

import com.hospital.oms.domain.Order;
import com.hospital.oms.resourceaccess.OrderAccess;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Component
public class OrderStorageEngine {

    private final OrderAccess orderAccess;

    public OrderStorageEngine(OrderAccess orderAccess) {
        this.orderAccess = orderAccess;
    }

    public void saveOrder(Order order) {
        orderAccess.saveOrder(order);
    }

    public Optional<Order> findOrderById(String id) {
        return orderAccess.findOrderById(id);
    }

    public List<Order> listPendingOrders() {
        return orderAccess.listPendingOrders();
    }

    public List<Order> listInProgressOrders() {
        return orderAccess.listInProgressOrders();
    }
}
