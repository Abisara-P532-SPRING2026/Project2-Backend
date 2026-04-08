package com.hospital.oms.strategy;

import com.hospital.oms.domain.Order;
import com.hospital.oms.domain.OrderType;
import com.hospital.oms.domain.Priority;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class DeadlineFirstTriageStrategy implements TriageStrategy {

    @Override
    public List<Order> sortPending(List<Order> pending, List<Order> inProgress) {
        List<Order> copy = new ArrayList<>(pending);
        copy.sort(
                Comparator.comparing(this::deadlineAt)
                        .thenComparing(Order::getCreatedAt));
        return copy;
    }

    private Instant deadlineAt(Order order) {
        return order.getCreatedAt().plus(targetTurnaround(order.getType(), order.getPriority()));
    }

    private Duration targetTurnaround(OrderType type, Priority priority) {
        return switch (type) {
            case LAB -> switch (priority) {
                case STAT -> Duration.ofSeconds(30);
                case URGENT -> Duration.ofSeconds(60);
                case ROUTINE -> Duration.ofSeconds(90);
            };
            case MEDICATION -> switch (priority) {
                case STAT -> Duration.ofSeconds(40);
                case URGENT -> Duration.ofSeconds(70);
                case ROUTINE -> Duration.ofSeconds(100);
            };
            case IMAGING -> switch (priority) {
                case STAT -> Duration.ofSeconds(50);
                case URGENT -> Duration.ofSeconds(80);
                case ROUTINE -> Duration.ofSeconds(110);
            };
        };
    }
}
