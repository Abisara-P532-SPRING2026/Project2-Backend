package com.hospital.oms.strategy;

import com.hospital.oms.domain.OrderType;

/** Selects active triage strategy per department/type. */
public interface DepartmentTriageSelector {

    TriageStrategy select(OrderType department);
}
