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
import com.hospital.oms.engine.OrderStorageEngine;
import com.hospital.oms.engine.TriagingEngine;
import com.hospital.oms.factory.OrderFactory;
import com.hospital.oms.handler.OrderProcessingContext;
import com.hospital.oms.handler.OrderProcessingHandler;
import com.hospital.oms.notification.NotificationService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;


@Service
public class OrderManager {

    private final OrderFactory orderFactory;
    private final OrderProcessingHandler submissionPipeline;
    private final OrderStorageEngine orderStorageEngine;
    private final TriagingEngine triagingEngine;
    private final NotificationService notificationService;
    private final InMemoryCommandLog commandLog;

    public OrderManager(
            OrderFactory orderFactory,
            OrderProcessingHandler submissionPipeline,
            OrderStorageEngine orderStorageEngine,
            TriagingEngine triagingEngine,
            NotificationService notificationService,
            InMemoryCommandLog commandLog) {
        this.orderFactory = orderFactory;
        this.submissionPipeline = submissionPipeline;
        this.orderStorageEngine = orderStorageEngine;
        this.triagingEngine = triagingEngine;
        this.notificationService = notificationService;
        this.commandLog = commandLog;
    }

    public void execute(OrderCommand command) {
        command.execute(this);
        commandLog.append(
                new CommandLogEntry(
                        Instant.now(), command.getCommandType(), command.getOrderId(), command.getActor()));
    }

    public void submit(SubmitOrderCommand cmd) {
        Order order =
                orderFactory.createOrder(
                        cmd.getType(),
                        cmd.getPatientName().trim(),
                        cmd.getOrderingClinicianId().trim(),
                        cmd.getClinicianName().trim(),
                        cmd.getDescription().trim(),
                        cmd.getPriority(),
                        Instant.now());
        submissionPipeline.handleSubmit(new OrderProcessingContext(order));
        cmd.setCreatedOrderId(order.getId());
        notificationService.notify(order, "SUBMITTED");
    }

    public void claim(ClaimOrderCommand cmd) {
        Order order =
                orderStorageEngine
                        .findOrderById(cmd.getOrderId())
                        .orElseThrow(() -> new IllegalArgumentException("Order not found: " + cmd.getOrderId()));
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be claimed.");
        }
        order.setStatus(OrderStatus.IN_PROGRESS);
        order.setClaimedByStaffId(cmd.getStaffId().trim());
        notificationService.notify(order, "CLAIMED");
    }

    public void complete(CompleteOrderCommand cmd) {
        Order order =
                orderStorageEngine
                        .findOrderById(cmd.getOrderId())
                        .orElseThrow(() -> new IllegalArgumentException("Order not found: " + cmd.getOrderId()));
        if (order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only IN_PROGRESS orders can be completed.");
        }
        if (order.getClaimedByStaffId() == null
                || !order.getClaimedByStaffId().equals(cmd.getStaffId().trim())) {
            throw new IllegalStateException("This order is locked to another staff member.");
        }
        order.setStatus(OrderStatus.COMPLETED);
        notificationService.notify(order, "COMPLETED");
    }

    public void cancel(CancelOrderCommand cmd) {
        Order order =
                orderStorageEngine
                        .findOrderById(cmd.getOrderId())
                        .orElseThrow(() -> new IllegalArgumentException("Order not found: " + cmd.getOrderId()));
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be cancelled.");
        }
        String expected = order.getOrderingClinicianId().trim();
        String provided = cmd.getClinicianId() == null ? "" : cmd.getClinicianId().trim();
        if (!expected.equals(provided)) {
            throw new IllegalStateException(
                    "Clinician ID does not match this order. Enter the same clinician id as on the submit form "
                            + "(not the name shown in the queue, and not fulfilment staff id).");
        }
        order.setStatus(OrderStatus.CANCELLED);
        notificationService.notify(order, "CANCELLED");
    }

    public java.util.List<Order> getPendingQueueSorted() {
        return triagingEngine.sortPendingQueue(orderStorageEngine.listPendingOrders());
    }

    public java.util.List<Order> getInProgressOrders() {
        return orderStorageEngine.listInProgressOrders();
    }

    public java.util.List<com.hospital.oms.commandlog.CommandLogEntry> getAuditTrail() {
        return commandLog.getEntriesNewestFirst();
    }

    public Optional<Order> getOrderById(String id) {
        return orderStorageEngine.findOrderById(id);
    }
}
