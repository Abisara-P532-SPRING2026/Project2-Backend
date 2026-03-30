package com.hospital.oms.domain;

import java.time.Instant;

public class MedicationOrder extends Order {

    public MedicationOrder(
            String id,
            String patientName,
            String orderingClinician,
            String description,
            Priority priority,
            Instant createdAt,
            OrderStatus status) {
        super(id, OrderType.MEDICATION, patientName, orderingClinician, description, priority, createdAt, status);
    }
}
