package com.hospital.oms.engine;

import com.hospital.oms.domain.LabOrder;
import com.hospital.oms.domain.Order;
import com.hospital.oms.domain.OrderStatus;
import com.hospital.oms.domain.Priority;
import com.hospital.oms.strategy.PriorityFirstTriageStrategy;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TriagingEngineTest {

    private final TriagingEngine engine = new TriagingEngine(new PriorityFirstTriageStrategy());

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
}
