package com.hospital.oms.manager;

import com.hospital.oms.command.CancelOrderCommand;
import com.hospital.oms.command.ClaimOrderCommand;
import com.hospital.oms.command.CompleteOrderCommand;
import com.hospital.oms.command.SubmitOrderCommand;
import com.hospital.oms.commandlog.InMemoryCommandLog;
import com.hospital.oms.domain.OrderStatus;
import com.hospital.oms.domain.OrderType;
import com.hospital.oms.domain.Priority;
import com.hospital.oms.engine.OrderStorageEngine;
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

    private OrderStorageEngine orderStorageEngine;
    private OrderManager orderManager;
    private NotificationService notifications;

    @BeforeEach
    void setUp() {
        OrderAccess access = new OrderAccess();
        orderStorageEngine = new OrderStorageEngine(access);
        notifications = Mockito.mock(NotificationService.class);
        OrderFactory factory = new OrderFactory();
        OrderProcessingHandler pipeline =
                new ValidatingOrderHandlerDecorator(new BaseOrderProcessingHandler(orderStorageEngine));
        TriagingEngine engine = new TriagingEngine(new PriorityFirstTriageStrategy());
        orderManager =
                new OrderManager(
                        factory,
                        pipeline,
                        orderStorageEngine,
                        engine,
                        notifications,
                        new InMemoryCommandLog());
    }

    @Test
    void submitThenClaimCompleteAndCancelPaths() {
        SubmitOrderCommand submit =
                new SubmitOrderCommand(OrderType.LAB, "pat", "cliId", "desc", Priority.ROUTINE, "Dr Cli");
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
                new SubmitOrderCommand(OrderType.MEDICATION, "p2", "c2id", "d2", Priority.ROUTINE, "Dr Two");
        orderManager.execute(second);
        String id2 = second.getOrderId();
        orderManager.execute(new CancelOrderCommand(id2, "c2id"));
        assertThat(orderManager.getOrderById(id2).orElseThrow().getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void cancelRejectsNonPending() {
        SubmitOrderCommand submit =
                new SubmitOrderCommand(OrderType.LAB, "p", "cid", "d", Priority.ROUTINE, "Dr C");
        orderManager.execute(submit);
        String id = submit.getOrderId();
        orderManager.execute(new ClaimOrderCommand(id, "staff1"));
        assertThatThrownBy(() -> orderManager.execute(new CancelOrderCommand(id, "cid")))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void cancelRejectsWrongClinician() {
        SubmitOrderCommand submit =
                new SubmitOrderCommand(OrderType.LAB, "p", "ownerId", "d", Priority.ROUTINE, "Dr Owner");
        orderManager.execute(submit);
        String id = submit.getOrderId();

        assertThatThrownBy(() -> orderManager.execute(new CancelOrderCommand(id, "otherId")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Clinician ID does not match");
    }

    @Test
    void cancelSucceedsWithMatchingClinician() {
        SubmitOrderCommand submit =
                new SubmitOrderCommand(OrderType.LAB, "p", "ownerId", "d", Priority.ROUTINE, "Dr Owner");
        orderManager.execute(submit);
        String id = submit.getOrderId();

        orderManager.execute(new CancelOrderCommand(id, "ownerId"));
        assertThat(orderManager.getOrderById(id).orElseThrow().getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void notificationsOnStatusChanges() {
        SubmitOrderCommand submit =
                new SubmitOrderCommand(OrderType.IMAGING, "p", "cid", "d", Priority.STAT, "Dr Stat");
        orderManager.execute(submit);
        String id = submit.getOrderId();
        orderManager.execute(new ClaimOrderCommand(id, "s"));
        orderManager.execute(new CompleteOrderCommand(id, "s"));

        ArgumentCaptor<String> events = ArgumentCaptor.forClass(String.class);
        Mockito.verify(notifications, Mockito.atLeast(3)).notify(Mockito.any(), events.capture());
        assertThat(events.getAllValues()).contains("SUBMITTED", "CLAIMED", "COMPLETED");
    }
}
