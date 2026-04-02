package com.hospital.oms.domain;

import java.time.Instant;

public class ImagingOrder extends Order {

    public ImagingOrder(
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
                OrderType.IMAGING,
                patientName,
                orderingClinicianId,
                orderingClinicianName,
                description,
                priority,
                createdAt,
                status);
    }
}
