package com.hospital.oms.manager;

import com.hospital.oms.command.CancelOrderCommand;
import com.hospital.oms.command.ClaimOrderCommand;
import com.hospital.oms.command.CompleteOrderCommand;
import com.hospital.oms.command.SubmitOrderCommand;
import com.hospital.oms.commandlog.InMemoryCommandLog;
import com.hospital.oms.domain.OrderStatus;
import com.hospital.oms.domain.OrderType;
import com.hospital.oms.domain.Priority;
import com.hospital.oms.engine.TriagingEngine;
import com.hospital.oms.factory.OrderFactory;
import com.hospital.oms.handler.BaseOrderProcessingHandler;
import com.hospital.oms.handler.OrderProcessingHandler;
import com.hospital.oms.handler.ValidatingOrderHandlerDecorator;
import com.hospital.oms.notification.NotificationService;
import com.hospital.oms.resourceaccess.OrderAccess;
import com.hospital.oms.strategy.PriorityFirstTriageStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderManagerTest {

    private OrderAccess orderAccess;
    private OrderManager orderManager;
    private NotificationService notifications;

    @BeforeEach
    void setUp() {
        orderAccess = new OrderAccess();
        notifications = Mockito.mock(NotificationService.class);
        OrderFactory factory = new OrderFactory();
        OrderProcessingHandler pipeline =
                new ValidatingOrderHandlerDecorator(new BaseOrderProcessingHandler(orderAccess));
        TriagingEngine engine = new TriagingEngine(new PriorityFirstTriageStrategy());
        orderManager =
                new OrderManager(
                        factory,
                        pipeline,
                        orderAccess,
                        engine,
                        notifications,
                        new InMemoryCommandLog());
    }

    @Test
    void submitThenClaimCompleteAndCancelPaths() {
        SubmitOrderCommand submit =
                new SubmitOrderCommand(OrderType.LAB, "pat", "cli", "desc", Priority.ROUTINE, "actor");
        orderManager.execute(submit);
        String id = submit.getOrderId();
        assertThat(id).isNotBlank();

        Mockito.verify(notifications, Mockito.times(1)).notify(Mockito.any(), Mockito.eq("SUBMITTED"));

        orderManager.execute(new ClaimOrderCommand(id, "staff1"));
        assertThat(orderManager.getOrderById(id).orElseThrow().getStatus()).isEqualTo(OrderStatus.IN_PROGRESS);
        assertThat(orderManager.getOrderById(id).orElseThrow().getClaimedByStaffId()).isEqualTo("staff1");

        assertThatThrownBy(() -> orderManager.execute(new ClaimOrderCommand(id, "staff2")))
                .isInstanceOf(IllegalStateException.class);

        orderManager.execute(new CompleteOrderCommand(id, "staff1"));
        assertThat(orderManager.getOrderById(id).orElseThrow().getStatus()).isEqualTo(OrderStatus.COMPLETED);

        SubmitOrderCommand second =
                new SubmitOrderCommand(OrderType.MEDICATION, "p2", "c2", "d2", Priority.ROUTINE, "a2");
        orderManager.execute(second);
        String id2 = second.getOrderId();
        orderManager.execute(new CancelOrderCommand(id2, "a2"));
        assertThat(orderManager.getOrderById(id2).orElseThrow().getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void cancelRejectsNonPending() {
        SubmitOrderCommand submit =
                new SubmitOrderCommand(OrderType.LAB, "p", "c", "d", Priority.ROUTINE, "a");
        orderManager.execute(submit);
        String id = submit.getOrderId();
        orderManager.execute(new ClaimOrderCommand(id, "staff1"));
        assertThatThrownBy(() -> orderManager.execute(new CancelOrderCommand(id, "a")))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void notificationsOnStatusChanges() {
        SubmitOrderCommand submit =
                new SubmitOrderCommand(OrderType.IMAGING, "p", "c", "d", Priority.STAT, "a");
        orderManager.execute(submit);
        String id = submit.getOrderId();
        orderManager.execute(new ClaimOrderCommand(id, "s"));
        orderManager.execute(new CompleteOrderCommand(id, "s"));

        ArgumentCaptor<String> events = ArgumentCaptor.forClass(String.class);
        Mockito.verify(notifications, Mockito.atLeast(3)).notify(Mockito.any(), events.capture());
        assertThat(events.getAllValues()).contains("SUBMITTED", "CLAIMED", "COMPLETED");
    }
}
