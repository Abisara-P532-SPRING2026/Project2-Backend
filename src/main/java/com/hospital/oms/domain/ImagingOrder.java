package com.hospital.oms.domain;

import java.time.Instant;

public class ImagingOrder extends Order {

    public ImagingOrder(
            String id,
            String patientName,
            String orderingClinician,
            String description,
            Priority priority,
            Instant createdAt,
            OrderStatus status) {
        super(id, OrderType.IMAGING, patientName, orderingClinician, description, priority, createdAt, status);
    }
}
