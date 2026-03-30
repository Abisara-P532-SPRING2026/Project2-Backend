package com.hospital.oms.domain;

import java.time.Instant;

public class LabOrder extends Order {

    public LabOrder(
            String id,
            String patientName,
            String orderingClinician,
            String description,
            Priority priority,
            Instant createdAt,
            OrderStatus status) {
        super(id, OrderType.LAB, patientName, orderingClinician, description, priority, createdAt, status);
    }
}
