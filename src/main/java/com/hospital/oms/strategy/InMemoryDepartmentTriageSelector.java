package com.hospital.oms.strategy;

import com.hospital.oms.domain.OrderType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

/** In-memory selector that supports runtime per-department strategy changes. */
@Component
public class InMemoryDepartmentTriageSelector implements DepartmentTriageSelector {

    private final Map<OrderType, TriageStrategy> selected = new EnumMap<>(OrderType.class);
    private final Map<TriageStrategyType, TriageStrategy> strategyByType = new EnumMap<>(TriageStrategyType.class);
    private final Map<OrderType, TriageStrategyType> selectedTypes = new EnumMap<>(OrderType.class);
    private final TriageStrategy fallback;

    public InMemoryDepartmentTriageSelector(
            PriorityFirstTriageStrategy priorityFirstTriageStrategy,
            LoadBalancingTriageStrategy loadBalancingTriageStrategy,
            DeadlineFirstTriageStrategy deadlineFirstTriageStrategy) {
        this.fallback = priorityFirstTriageStrategy;
        strategyByType.put(TriageStrategyType.PRIORITY_FIRST, priorityFirstTriageStrategy);
        strategyByType.put(TriageStrategyType.LOAD_BALANCING, loadBalancingTriageStrategy);
        strategyByType.put(TriageStrategyType.DEADLINE_FIRST, deadlineFirstTriageStrategy);
        for (OrderType t : OrderType.values()) {
            selected.put(t, priorityFirstTriageStrategy);
            selectedTypes.put(t, TriageStrategyType.PRIORITY_FIRST);
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

    public void set(OrderType department, TriageStrategyType strategyType) {
        TriageStrategy strategy = strategyByType.get(strategyType);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported triage strategy: " + strategyType);
        }
        selected.put(department, strategy);
        selectedTypes.put(department, strategyType);
    }

    public TriageStrategyType getSelectedType(OrderType department) {
        if (department == null) {
            return TriageStrategyType.PRIORITY_FIRST;
        }
        return selectedTypes.getOrDefault(department, TriageStrategyType.PRIORITY_FIRST);
    }
}
