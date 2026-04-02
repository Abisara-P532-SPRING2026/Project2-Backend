package com.hospital.oms.web.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.hospital.oms.domain.OrderType;
import com.hospital.oms.domain.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record SubmitOrderRequest(
        @NotNull OrderType orderType,
        @NotBlank String patientName,
        @NotBlank @JsonAlias("orderingClinician") String orderingClinicianId,
        @NotBlank String description,
        @NotNull Priority priority,
        @NotBlank @JsonAlias("actor") String clinicianName) {}
