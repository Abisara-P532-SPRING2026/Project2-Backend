package com.hospital.oms.strategy;

import com.hospital.oms.domain.Order;
import com.hospital.oms.domain.OrderType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

@Component
public class LoadBalancingTriageStrategy implements TriageStrategy {

    @Override
    public List<Order> sortPending(List<Order> pending, List<Order> inProgress) {
        List<Order> fifo = new ArrayList<>(pending);
        fifo.sort(Comparator.comparing(Order::getCreatedAt));

        Map<String, Integer> inProgressCountByStaff = buildLoadMap(fifo, inProgress);
        if (inProgressCountByStaff.isEmpty()) {
            return fifo;
        }

        PriorityQueue<StaffLoad> loads =
                new PriorityQueue<>(Comparator.comparingInt(StaffLoad::load).thenComparing(StaffLoad::staffId));
        inProgressCountByStaff.forEach((staffId, load) -> loads.add(new StaffLoad(staffId, load)));

        List<ScheduledOrder> scheduled = new ArrayList<>();
        for (Order order : fifo) {
            StaffLoad leastLoaded = loads.poll();
            int currentLoad = leastLoaded.load();
            scheduled.add(new ScheduledOrder(order, currentLoad));
            loads.add(new StaffLoad(leastLoaded.staffId(), currentLoad + 1));
        }

        scheduled.sort(
                Comparator.comparingInt(ScheduledOrder::assignedLoad)
                        .thenComparing(s -> s.order().getCreatedAt()));
        return scheduled.stream().map(ScheduledOrder::order).toList();
    }

    private static Map<String, Integer> buildLoadMap(List<Order> pending, List<Order> inProgress) {
        Map<String, Integer> countByStaff = new HashMap<>();
        Set<OrderType> departmentsInPending = pending.stream().map(Order::getType).collect(java.util.stream.Collectors.toSet());
        for (Order order : inProgress) {
            String staffId = order.getClaimedByStaffId();
            if (staffId == null || staffId.isBlank()) {
                continue;
            }
            if (!departmentsInPending.contains(order.getType())) {
                continue;
            }
            countByStaff.merge(staffId, 1, Integer::sum);
        }
        return countByStaff;
    }

    private record StaffLoad(String staffId, int load) {}

    private record ScheduledOrder(Order order, int assignedLoad) {}
}
