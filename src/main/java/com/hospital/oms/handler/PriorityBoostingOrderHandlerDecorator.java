package com.hospital.oms.handler;

import com.hospital.oms.domain.Order;
import com.hospital.oms.domain.Priority;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Optional;

public class PriorityBoostingOrderHandlerDecorator implements OrderProcessingHandler {

    private static final Duration ESCALATION_WINDOW = Duration.ofMinutes(5);
    private final OrderProcessingHandler delegate;

    public PriorityBoostingOrderHandlerDecorator(OrderProcessingHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public void handleSubmit(OrderProcessingContext context) {
        Order order = context.getOrder();
        if (order.getPriority() == Priority.URGENT) {
            Optional<Order> latestStatOfSameType =
                    context.getOrderStorageEngine().listAllOrders().stream()
                            .filter(o -> o.getType() == order.getType())
                            .filter(o -> o.getPriority() == Priority.STAT)
                            .max(Comparator.comparing(Order::getCreatedAt));
            if (latestStatOfSameType.isPresent()) {
                Instant statTime = latestStatOfSameType.get().getCreatedAt();
                Duration sinceStat = Duration.between(statTime, order.getCreatedAt());
                if (!sinceStat.isNegative() && sinceStat.compareTo(ESCALATION_WINDOW) <= 0) {
                    order.setPriority(Priority.STAT);
                    context.addAuditDetail("escalatedToStat", "true");
                    context.addAuditDetail("escalationReason", "urgentWithin5mOfStatSameType");
                    context.addAuditDetail("triggerStatOrderId", latestStatOfSameType.get().getId());
                } else {
                    context.addAuditDetail("escalatedToStat", "false");
                    context.addAuditDetail("escalationReason", "outside5mWindow");
                }
            } else {
                context.addAuditDetail("escalatedToStat", "false");
                context.addAuditDetail("escalationReason", "noRecentStatOfSameType");
            }
        }
        delegate.handleSubmit(context);
    }
}
