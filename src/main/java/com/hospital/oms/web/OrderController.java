package com.hospital.oms.web;

import com.hospital.oms.command.CancelOrderCommand;
import com.hospital.oms.command.ClaimOrderCommand;
import com.hospital.oms.command.CompleteOrderCommand;
import com.hospital.oms.command.OrderCommand;
import com.hospital.oms.command.SubmitOrderCommand;
import com.hospital.oms.manager.OrderManager;
import com.hospital.oms.web.dto.CancelRequest;
import com.hospital.oms.web.dto.CommandLogResponse;
import com.hospital.oms.web.dto.OrderResponse;
import com.hospital.oms.web.dto.StaffActionRequest;
import com.hospital.oms.web.dto.SubmitOrderRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderManager orderManager;

    public OrderController(OrderManager orderManager) {
        this.orderManager = orderManager;
    }

    @GetMapping("/orders/pending-queue")
    public List<OrderResponse> pendingQueue() {
        return orderManager.getPendingQueueSorted().stream().map(OrderResponse::from).toList();
    }

    @GetMapping("/orders/in-progress")
    public List<OrderResponse> inProgress() {
        return orderManager.getInProgressOrders().stream().map(OrderResponse::from).toList();
    }

    @PostMapping("/orders")
    public OrderResponse submit(@Valid @RequestBody SubmitOrderRequest request) {
        SubmitOrderCommand cmd =
                new SubmitOrderCommand(
                        request.orderType(),
                        request.patientName(),
                        request.orderingClinicianId(),
                        request.description(),
                        request.priority(),
                        request.clinicianName());
        orderManager.execute(cmd);
        return OrderResponse.from(
                orderManager
                        .getOrderById(cmd.getOrderId())
                        .orElseThrow(() -> new IllegalStateException("Order not found after submit")));
    }

    @PostMapping("/orders/{id}/claim")
    public OrderResponse claim(@PathVariable String id, @Valid @RequestBody StaffActionRequest body) {
        OrderCommand cmd = new ClaimOrderCommand(id, body.staffId());
        orderManager.execute(cmd);
        return OrderResponse.from(
                orderManager
                        .getOrderById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id)));
    }

    @PostMapping("/orders/{id}/complete")
    public OrderResponse complete(@PathVariable String id, @Valid @RequestBody StaffActionRequest body) {
        OrderCommand cmd = new CompleteOrderCommand(id, body.staffId());
        orderManager.execute(cmd);
        return OrderResponse.from(
                orderManager
                        .getOrderById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id)));
    }

    @PostMapping("/orders/{id}/cancel")
    public OrderResponse cancel(@PathVariable String id, @Valid @RequestBody CancelRequest body) {
        OrderCommand cmd = new CancelOrderCommand(id, body.clinicianId().trim());
        orderManager.execute(cmd);
        return OrderResponse.from(
                orderManager
                        .getOrderById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id)));
    }

    @GetMapping("/audit")
    public List<CommandLogResponse> audit() {
        return orderManager.getAuditTrail().stream().map(CommandLogResponse::from).toList();
    }
}
