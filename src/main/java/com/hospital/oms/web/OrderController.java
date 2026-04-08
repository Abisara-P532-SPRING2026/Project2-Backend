package com.hospital.oms.web;

import com.hospital.oms.command.CancelOrderCommand;
import com.hospital.oms.command.ClaimOrderCommand;
import com.hospital.oms.command.CompleteOrderCommand;
import com.hospital.oms.command.OrderCommand;
import com.hospital.oms.command.SubmitOrderCommand;
import com.hospital.oms.domain.OrderType;
import com.hospital.oms.manager.OrderManager;
import com.hospital.oms.strategy.InMemoryDepartmentTriageSelector;
import com.hospital.oms.web.dto.CancelRequest;
import com.hospital.oms.web.dto.CommandLogResponse;
import com.hospital.oms.web.dto.DepartmentTriageConfigResponse;
import com.hospital.oms.web.dto.DepartmentTriageUpdateRequest;
import com.hospital.oms.web.dto.OrderResponse;
import com.hospital.oms.web.dto.StaffActionRequest;
import com.hospital.oms.web.dto.SubmitOrderRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderManager orderManager;
    private final InMemoryDepartmentTriageSelector triageSelector;

    public OrderController(OrderManager orderManager, InMemoryDepartmentTriageSelector triageSelector) {
        this.orderManager = orderManager;
        this.triageSelector = triageSelector;
    }

    @GetMapping("/orders/pending-queue")
    public List<OrderResponse> pendingQueue(@RequestParam(required = false) OrderType department) {
        if (department == null) {
            return orderManager.getPendingQueueSorted().stream().map(OrderResponse::from).toList();
        }
        return orderManager.getPendingQueueSorted(department).stream().map(OrderResponse::from).toList();
    }

    @GetMapping("/orders/in-progress")
    public List<OrderResponse> inProgress() {
        return orderManager.getInProgressOrders().stream().map(OrderResponse::from).toList();
    }

    @PostMapping("/orders")
    public OrderResponse submit(@Valid @RequestBody SubmitOrderRequest request) {
        SubmitOrderCommand cmd =
                new SubmitOrderCommand(
                        request.getOrderType(),
                        request.getPatientName(),
                        request.getOrderingClinicianId(),
                        request.getDescription(),
                        request.getPriority(),
                        request.getClinicianName());
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
        OrderCommand cmd = new CancelOrderCommand(id, body.getClinicianId().trim());
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

    @GetMapping("/triage/departments/{department}")
    public DepartmentTriageConfigResponse getDepartmentTriage(@PathVariable OrderType department) {
        return new DepartmentTriageConfigResponse(department, triageSelector.getSelectedType(department));
    }

    @PostMapping("/triage/departments/{department}")
    public DepartmentTriageConfigResponse updateDepartmentTriage(
            @PathVariable OrderType department, @Valid @RequestBody DepartmentTriageUpdateRequest body) {
        triageSelector.set(department, body.strategy());
        return new DepartmentTriageConfigResponse(department, triageSelector.getSelectedType(department));
    }

    @PostMapping("/admin/undo")
    public Map<String, String> undoLastCommand() {
        orderManager.undoLastCommand();
        return Map.of("status", "OK", "message", "Last command undone.");
    }

    @PostMapping("/admin/replay/{auditIndex}")
    public Map<String, String> replayByAuditIndex(@PathVariable int auditIndex) {
        orderManager.replayByAuditIndex(auditIndex);
        return Map.of("status", "OK", "message", "Command replayed.", "auditIndex", String.valueOf(auditIndex));
    }
}
