package com.hospital.oms.engine;

import com.hospital.oms.domain.LabOrder;
import com.hospital.oms.domain.Order;
import com.hospital.oms.domain.OrderStatus;
import com.hospital.oms.domain.OrderType;
import com.hospital.oms.domain.Priority;
import com.hospital.oms.resourceaccess.OrderAccess;
import com.hospital.oms.strategy.DeadlineFirstTriageStrategy;
import com.hospital.oms.strategy.InMemoryDepartmentTriageSelector;
import com.hospital.oms.strategy.LoadBalancingTriageStrategy;
import com.hospital.oms.strategy.PriorityFirstTriageStrategy;
import com.hospital.oms.strategy.TriageStrategyType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TriagingEngineTest {

    private final InMemoryDepartmentTriageSelector selector =
            new InMemoryDepartmentTriageSelector(
                    new PriorityFirstTriageStrategy(),
                    new LoadBalancingTriageStrategy(),
                    new DeadlineFirstTriageStrategy());

    private final TriagingEngine engine =
            new TriagingEngine(selector, new OrderStorageEngine(new OrderAccess()));

    @Test
    void sortsStatBeforeUrgentBeforeRoutineThenFifo() {
        Instant t0 = Instant.parse("2025-01-01T10:00:00Z");
        Instant t1 = Instant.parse("2025-01-01T10:01:00Z");
        Order r = new LabOrder("r", "p", "c1", "Dr A", "d", Priority.ROUTINE, t1, OrderStatus.PENDING);
        Order u = new LabOrder("u", "p", "c1", "Dr A", "d", Priority.URGENT, t0, OrderStatus.PENDING);
        Order s = new LabOrder("s", "p", "c1", "Dr A", "d", Priority.STAT, t1, OrderStatus.PENDING);
        Order r2 = new LabOrder("r2", "p", "c1", "Dr A", "d", Priority.ROUTINE, t0, OrderStatus.PENDING);

        List<Order> pending = new ArrayList<>(List.of(r, u, s, r2));
        List<Order> sorted = engine.sortPendingQueue(pending);

        assertThat(sorted).extracting(Order::getId).containsExactly("s", "u", "r2", "r");
    }

    @Test
    void deadlineFirstPrioritizesSoonestDeadline() {
        selector.set(OrderType.LAB, TriageStrategyType.DEADLINE_FIRST);
        Instant base = Instant.parse("2025-01-01T10:00:00Z");
        Order urgent = new LabOrder("u", "p", "c1", "Dr A", "d", Priority.URGENT, base, OrderStatus.PENDING);
        Order stat =
                new LabOrder(
                        "s",
                        "p",
                        "c1",
                        "Dr A",
                        "d",
                        Priority.STAT,
                        base.plusSeconds(20 * 60),
                        OrderStatus.PENDING);
        Order routine = new LabOrder("r", "p", "c1", "Dr A", "d", Priority.ROUTINE, base, OrderStatus.PENDING);

        List<Order> sorted = engine.sortPendingQueue(List.of(routine, urgent, stat), OrderType.LAB);

        assertThat(sorted).extracting(Order::getId).containsExactly("s", "u", "r");
    }
}
