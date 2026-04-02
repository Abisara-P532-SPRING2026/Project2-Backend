package com.hospital.oms.domain;

import java.time.Instant;
import java.util.Objects;


public abstract class Order {

    private final String id;
    private final OrderType type;
    private final String patientName;
    private final String orderingClinicianId;
    private final String orderingClinicianName;
    private final String description;
    private final Priority priority;
    private final Instant createdAt;

    private OrderStatus status;
    private String claimedByStaffId;

    protected Order(
            String id,
            OrderType type,
            String patientName,
            String orderingClinicianId,
            String orderingClinicianName,
            String description,
            Priority priority,
            Instant createdAt,
            OrderStatus status) {
        this.id = Objects.requireNonNull(id);
        this.type = Objects.requireNonNull(type);
        this.patientName = Objects.requireNonNull(patientName);
        this.orderingClinicianId = Objects.requireNonNull(orderingClinicianId);
        this.orderingClinicianName = Objects.requireNonNull(orderingClinicianName);
        this.description = Objects.requireNonNull(description);
        this.priority = Objects.requireNonNull(priority);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.status = Objects.requireNonNull(status);
    }

    public String getId() {
        return id;
    }

    public OrderType getType() {
        return type;
    }

    public String getPatientName() {
        return patientName;
    }

    /** System id used for cancel verification (not shown as the primary label in the queue UI). */
    public String getOrderingClinicianId() {
        return orderingClinicianId;
    }

    /** Display name for queue and notifications. */
    public String getOrderingClinicianName() {
        return orderingClinicianName;
    }

    public String getDescription() {
        return description;
    }

    public Priority getPriority() {
        return priority;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = Objects.requireNonNull(status);
    }

    public String getClaimedByStaffId() {
        return claimedByStaffId;
    }

    public void setClaimedByStaffId(String claimedByStaffId) {
        this.claimedByStaffId = claimedByStaffId;
    }
}
