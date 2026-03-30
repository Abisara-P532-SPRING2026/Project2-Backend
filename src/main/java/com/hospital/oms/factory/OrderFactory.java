package com.hospital.oms.factory;

import com.hospital.oms.domain.ImagingOrder;
import com.hospital.oms.domain.LabOrder;
import com.hospital.oms.domain.MedicationOrder;
import com.hospital.oms.domain.Order;
import com.hospital.oms.domain.OrderStatus;
import com.hospital.oms.domain.OrderType;
import com.hospital.oms.domain.Priority;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

/**
 * Factory pattern: single creation point for concrete order subtypes.
 */
@Component
public class OrderFactory {

    public Order createOrder(
            OrderType type,
            String patientName,
            String orderingClinician,
            String description,
            Priority priority,
            Instant createdAt) {
        String id = UUID.randomUUID().toString();
        return switch (type) {
            case LAB -> new LabOrder(id, patientName, orderingClinician, description, priority, createdAt, OrderStatus.PENDING);
            case MEDICATION -> new MedicationOrder(
                    id, patientName, orderingClinician, description, priority, createdAt, OrderStatus.PENDING);
            case IMAGING -> new ImagingOrder(id, patientName, orderingClinician, description, priority, createdAt, OrderStatus.PENDING);
        };
    }
}
