package com.hospital.oms.domain;

import java.time.Instant;

public class MedicationOrder extends Order {

    public MedicationOrder(
            String id,
            String patientName,
            String orderingClinicianId,
            String orderingClinicianName,
            String description,
            Priority priority,
            Instant createdAt,
            OrderStatus status) {
        super(
                id,
                OrderType.MEDICATION,
                patientName,
                orderingClinicianId,
                orderingClinicianName,
                description,
                priority,
                createdAt,
                status);
    }
}
