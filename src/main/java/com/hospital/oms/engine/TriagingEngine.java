package com.hospital.oms.engine;

import com.hospital.oms.domain.Order;
import com.hospital.oms.domain.OrderType;
import com.hospital.oms.strategy.DepartmentTriageSelector;
import com.hospital.oms.strategy.TriageStrategy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class TriagingEngine {

    private final DepartmentTriageSelector triageSelector;
    private final OrderStorageEngine orderStorageEngine;

    public TriagingEngine(DepartmentTriageSelector triageSelector, OrderStorageEngine orderStorageEngine) {
        this.triageSelector = triageSelector;
        this.orderStorageEngine = orderStorageEngine;
    }

    public List<Order> sortPendingQueue(List<Order> pending) {
        return sortPendingQueue(pending, null);
    }

    public List<Order> sortPendingQueue(List<Order> pending, OrderType department) {
        TriageStrategy strategy = triageSelector.select(department);
        return strategy.sortPending(new ArrayList<>(pending), orderStorageEngine.listInProgressOrders());
    }
}
