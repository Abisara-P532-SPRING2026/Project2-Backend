package com.hospital.oms.web.dto;

import com.hospital.oms.strategy.TriageStrategyType;
import jakarta.validation.constraints.NotNull;

public record DepartmentTriageUpdateRequest(@NotNull TriageStrategyType strategy) {}
