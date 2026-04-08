package com.hospital.oms.handler;

import com.hospital.oms.domain.Order;
import com.hospital.oms.engine.OrderStorageEngine;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class OrderProcessingContext {

    private final Order order;
    private final OrderStorageEngine orderStorageEngine;
    private final Map<String, String> auditDetails = new LinkedHashMap<>();

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

    public void addAuditDetail(String key, String value) {
        auditDetails.put(key, value);
    }

    public Map<String, String> getAuditDetails() {
        return Collections.unmodifiableMap(auditDetails);
    }
}
