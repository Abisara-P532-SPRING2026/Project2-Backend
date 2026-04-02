package com.hospital.oms.resourceaccess;

import com.hospital.oms.domain.Order;
import com.hospital.oms.domain.OrderStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class OrderAccess {

    private final Map<String, Order> orders = new ConcurrentHashMap<>();

    public void saveOrder(Order order) {
        orders.put(order.getId(), order);
    }

    public Optional<Order> findOrderById(String id) {
        return Optional.ofNullable(orders.get(id));
    }

    public List<Order> listPendingOrders() {
        return filterByStatus(OrderStatus.PENDING);
    }

    public List<Order> listInProgressOrders() {
        return filterByStatus(OrderStatus.IN_PROGRESS);
    }

    public List<Order> listAllOrders() {
        return new ArrayList<>(orders.values());
    }

    private List<Order> filterByStatus(OrderStatus status) {
        List<Order> result = new ArrayList<>();
        for (Order o : orders.values()) {
            if (o.getStatus() == status) {
                result.add(o);
            }
        }
        return result;
    }

    public Collection<Order> allOrdersSnapshot() {
        return List.copyOf(orders.values());
    }
}
