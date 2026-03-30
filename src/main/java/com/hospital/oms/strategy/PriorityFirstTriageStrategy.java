package com.hospital.oms.strategy;

import com.hospital.oms.domain.Order;
import com.hospital.oms.domain.Priority;
import org.springframework.stereotype.Component;

/**
 * Week 1: STAT before URGENT before ROUTINE; ties broken by submission time (FIFO).
 */
@Component
public class PriorityFirstTriageStrategy implements TriageStrategy {

    @Override
    public int compareQueuePosition(Order a, Order b) {
        int pr = Integer.compare(priorityRank(b.getPriority()), priorityRank(a.getPriority()));
        if (pr != 0) {
            return pr;
        }
        return a.getCreatedAt().compareTo(b.getCreatedAt());
    }

    private static int priorityRank(Priority p) {
        return switch (p) {
            case STAT -> 3;
            case URGENT -> 2;
            case ROUTINE -> 1;
        };
    }
}
