package com.hospital.oms.web.dto;

import com.hospital.oms.domain.Order;
import com.hospital.oms.domain.OrderStatus;
import com.hospital.oms.domain.OrderType;
import com.hospital.oms.domain.Priority;

import java.time.Instant;

public record OrderResponse(
        String id,
        OrderType type,
        String patientName,
        String orderingClinician,
        String description,
        Priority priority,
        OrderStatus status,
        Instant createdAt,
        String claimedByStaffId) {

    public static OrderResponse from(Order o) {
        return new OrderResponse(
                o.getId(),
                o.getType(),
                o.getPatientName(),
                o.getOrderingClinician(),
                o.getDescription(),
                o.getPriority(),
                o.getStatus(),
                o.getCreatedAt(),
                o.getClaimedByStaffId());
    }
}
