package com.hospital.oms.domain;

import java.time.Instant;
import java.util.Objects;

/**
 * Resource layer: shared clinical order aggregate.
 */
public abstract class Order {

    private final String id;
    private final OrderType type;
    private final String patientName;
    private final String orderingClinician;
    private final String description;
    private final Priority priority;
    private final Instant createdAt;

    private OrderStatus status;
    private String claimedByStaffId;

    protected Order(
            String id,
            OrderType type,
            String patientName,
            String orderingClinician,
            String description,
            Priority priority,
            Instant createdAt,
            OrderStatus status) {
        this.id = Objects.requireNonNull(id);
        this.type = Objects.requireNonNull(type);
        this.patientName = Objects.requireNonNull(patientName);
        this.orderingClinician = Objects.requireNonNull(orderingClinician);
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

    public String getOrderingClinician() {
        return orderingClinician;
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
