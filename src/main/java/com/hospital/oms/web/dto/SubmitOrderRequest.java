package com.hospital.oms.web.dto;

import com.hospital.oms.domain.OrderType;
import com.hospital.oms.domain.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubmitOrderRequest(
        @NotNull OrderType orderType,
        @NotBlank String patientName,
        @NotBlank String orderingClinician,
        @NotBlank String description,
        @NotNull Priority priority,
        @NotBlank String actor) {}
