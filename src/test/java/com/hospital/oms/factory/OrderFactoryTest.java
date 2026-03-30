package com.hospital.oms.factory;

import com.hospital.oms.domain.ImagingOrder;
import com.hospital.oms.domain.LabOrder;
import com.hospital.oms.domain.MedicationOrder;
import com.hospital.oms.domain.Order;
import com.hospital.oms.domain.OrderStatus;
import com.hospital.oms.domain.OrderType;
import com.hospital.oms.domain.Priority;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class OrderFactoryTest {

    private final OrderFactory factory = new OrderFactory();

    @Test
    void createsLabMedicationImagingSubtypes() {
        Instant now = Instant.now();
        Order lab = factory.createOrder(OrderType.LAB, "a", "b", "c", Priority.ROUTINE, now);
        Order med = factory.createOrder(OrderType.MEDICATION, "a", "b", "c", Priority.ROUTINE, now);
        Order img = factory.createOrder(OrderType.IMAGING, "a", "b", "c", Priority.ROUTINE, now);

        assertThat(lab).isInstanceOf(LabOrder.class);
        assertThat(med).isInstanceOf(MedicationOrder.class);
        assertThat(img).isInstanceOf(ImagingOrder.class);
        assertThat(lab.getStatus()).isEqualTo(OrderStatus.PENDING);
    }
}
