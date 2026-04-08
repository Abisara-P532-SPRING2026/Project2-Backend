package com.hospital.oms.web.dto;

import com.hospital.oms.domain.OrderType;
import com.hospital.oms.strategy.TriageStrategyType;

public record DepartmentTriageConfigResponse(OrderType department, TriageStrategyType strategy) {}
