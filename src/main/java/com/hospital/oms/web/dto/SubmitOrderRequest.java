package com.hospital.oms.web.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.hospital.oms.domain.OrderType;
import com.hospital.oms.domain.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * JavaBean (not a record): Jackson + {@code @JsonAlias} on record constructor parameters breaks
 * when the same logical field appears twice in JSON (e.g. {@code orderingClinicianId} and
 * {@code orderingClinician}); field + setter binding accepts aliases reliably.
 */
public class SubmitOrderRequest {

    @NotNull private OrderType orderType;

    @NotBlank private String patientName;

    @NotBlank
    @JsonAlias("orderingClinician")
    private String orderingClinicianId;

    @NotBlank private String description;

    @NotNull private Priority priority;

    @NotBlank
    @JsonAlias("actor")
    private String clinicianName;

    public SubmitOrderRequest() {}

    public SubmitOrderRequest(
            OrderType orderType,
            String patientName,
            String orderingClinicianId,
            String description,
            Priority priority,
            String clinicianName) {
        this.orderType = orderType;
        this.patientName = patientName;
        this.orderingClinicianId = orderingClinicianId;
        this.description = description;
        this.priority = priority;
        this.clinicianName = clinicianName;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getOrderingClinicianId() {
        return orderingClinicianId;
    }

    public void setOrderingClinicianId(String orderingClinicianId) {
        this.orderingClinicianId = orderingClinicianId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getClinicianName() {
        return clinicianName;
    }

    public void setClinicianName(String clinicianName) {
        this.clinicianName = clinicianName;
    }
}
