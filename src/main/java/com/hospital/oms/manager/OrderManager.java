package com.hospital.oms.manager;

import com.hospital.oms.command.CancelOrderCommand;
import com.hospital.oms.command.ClaimOrderCommand;
import com.hospital.oms.command.CompleteOrderCommand;
import com.hospital.oms.command.OrderCommand;
import com.hospital.oms.command.SubmitOrderCommand;
import com.hospital.oms.commandlog.CommandLogEntry;
import com.hospital.oms.commandlog.InMemoryCommandLog;
import com.hospital.oms.domain.Order;
import com.hospital.oms.domain.OrderStatus;
import com.hospital.oms.engine.TriagingEngine;
import com.hospital.oms.factory.OrderFactory;
import com.hospital.oms.handler.OrderProcessingContext;
import com.hospital.oms.handler.OrderProcessingHandler;
import com.hospital.oms.notification.NotificationService;
import com.hospital.oms.resourceaccess.OrderAccess;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

/**
 * Manager layer: orchestrates order use cases; entry point for commands and engines.
 */
@Service
public class OrderManager {

    private final OrderFactory orderFactory;
    private final OrderProcessingHandler submissionPipeline;
    private final OrderAccess orderAccess;
    private final TriagingEngine triagingEngine;
    private final NotificationService notificationService;
    private final InMemoryCommandLog commandLog;

    public OrderManager(
            OrderFactory orderFactory,
            OrderProcessingHandler submissionPipeline,
            OrderAccess orderAccess,
            TriagingEngine triagingEngine,
            NotificationService notificationService,
            InMemoryCommandLog commandLog) {
        this.orderFactory = orderFactory;
        this.submissionPipeline = submissionPipeline;
        this.orderAccess = orderAccess;
        this.triagingEngine = triagingEngine;
        this.notificationService = notificationService;
        this.commandLog = commandLog;
    }

    public void execute(OrderCommand command) {
        command.execute(this);
        commandLog.append(
                new CommandLogEntry(Instant.now(), command.getCommandType(), command.getOrderId(), command.getActor()));
    }

    public void submit(SubmitOrderCommand cmd) {
        Order order =
                orderFactory.createOrder(
                        cmd.getType(),
                        cmd.getPatientName(),
                        cmd.getOrderingClinician(),
                        cmd.getDescription(),
                        cmd.getPriority(),
                        Instant.now());
        submissionPipeline.handleSubmit(new OrderProcessingContext(order));
        cmd.setCreatedOrderId(order.getId());
        notificationService.notify(order, "SUBMITTED");
    }

    public void claim(ClaimOrderCommand cmd) {
        Order order =
                orderAccess
                        .findOrderById(cmd.getOrderId())
                        .orElseThrow(() -> new IllegalArgumentException("Order not found: " + cmd.getOrderId()));
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be claimed.");
        }
        order.setStatus(OrderStatus.IN_PROGRESS);
        order.setClaimedByStaffId(cmd.getStaffId());
        notificationService.notify(order, "CLAIMED");
    }

    public void complete(CompleteOrderCommand cmd) {
        Order order =
                orderAccess
                        .findOrderById(cmd.getOrderId())
                        .orElseThrow(() -> new IllegalArgumentException("Order not found: " + cmd.getOrderId()));
        if (order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only IN_PROGRESS orders can be completed.");
        }
        if (order.getClaimedByStaffId() == null || !order.getClaimedByStaffId().equals(cmd.getStaffId())) {
            throw new IllegalStateException("This order is locked to another staff member.");
        }
        order.setStatus(OrderStatus.COMPLETED);
        notificationService.notify(order, "COMPLETED");
    }

    public void cancel(CancelOrderCommand cmd) {
        Order order =
                orderAccess
                        .findOrderById(cmd.getOrderId())
                        .orElseThrow(() -> new IllegalArgumentException("Order not found: " + cmd.getOrderId()));
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be cancelled.");
        }
        order.setStatus(OrderStatus.CANCELLED);
        notificationService.notify(order, "CANCELLED");
    }

    public java.util.List<Order> getPendingQueueSorted() {
        return triagingEngine.sortPendingQueue(orderAccess.listPendingOrders());
    }

    public java.util.List<Order> getInProgressOrders() {
        return orderAccess.listInProgressOrders();
    }

    public java.util.List<com.hospital.oms.commandlog.CommandLogEntry> getAuditTrail() {
        return commandLog.getEntriesNewestFirst();
    }

    public Optional<Order> getOrderById(String id) {
        return orderAccess.findOrderById(id);
    }
}
