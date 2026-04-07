package com.hospital.oms.strategy;

import com.hospital.oms.domain.Order;
import com.hospital.oms.domain.Priority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class PriorityFirstTriageStrategy implements TriageStrategy {

    @Override
    public List<Order> sortPending(List<Order> pending, List<Order> inProgress) {
        List<Order> copy = new ArrayList<>(pending);
        copy.sort(
                Comparator.comparingInt((Order o) -> priorityRank(o.getPriority()))
                        .reversed()
                        .thenComparing(Order::getCreatedAt));
        return copy;
    }

    private static int priorityRank(Priority p) {
        return switch (p) {
            case STAT -> 3;
            case URGENT -> 2;
            case ROUTINE -> 1;
        };
    }
}
