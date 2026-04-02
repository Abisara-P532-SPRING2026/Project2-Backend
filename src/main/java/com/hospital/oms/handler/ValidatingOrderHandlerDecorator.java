package com.hospital.oms.handler;

import com.hospital.oms.domain.Order;

public class ValidatingOrderHandlerDecorator implements OrderProcessingHandler {

    private final OrderProcessingHandler delegate;

    public ValidatingOrderHandlerDecorator(OrderProcessingHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public void handleSubmit(OrderProcessingContext context) {
        Order o = context.getOrder();
        if (o.getPatientName().isBlank()
                || o.getOrderingClinicianId().isBlank()
                || o.getOrderingClinicianName().isBlank()
                || o.getDescription().isBlank()) {
            throw new IllegalArgumentException(
                    "Patient, clinician id, clinician name, and description are required.");
        }
        delegate.handleSubmit(context);
    }
}
