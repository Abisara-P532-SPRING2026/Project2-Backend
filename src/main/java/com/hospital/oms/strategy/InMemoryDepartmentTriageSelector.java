package com.hospital.oms.strategy;

import com.hospital.oms.domain.OrderType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

/*  Week1 default selector: all departments use priority-first until overridden by future settings endpoint. */
@Component
public class InMemoryDepartmentTriageSelector implements DepartmentTriageSelector {

    private final Map<OrderType, TriageStrategy> selected = new EnumMap<>(OrderType.class);
    private final TriageStrategy fallback;

    public InMemoryDepartmentTriageSelector(PriorityFirstTriageStrategy priorityFirstTriageStrategy) {
        this.fallback = priorityFirstTriageStrategy;
        for (OrderType t : OrderType.values()) {
            selected.put(t, priorityFirstTriageStrategy);
        }
    }

    @Override
    public TriageStrategy select(OrderType department) {
        if (department == null) {
            return fallback;
        }
        return selected.getOrDefault(department, fallback);
    }

    public void set(OrderType department, TriageStrategy strategy) {
        selected.put(department, strategy);
    }
}
