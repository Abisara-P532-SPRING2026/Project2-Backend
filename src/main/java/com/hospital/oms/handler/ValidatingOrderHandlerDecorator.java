package com.hospital.oms.handler;

import com.hospital.oms.domain.Order;
/**
 * Decorator: validation before persistence; base handler stays unchanged.
 */
public class ValidatingOrderHandlerDecorator implements OrderProcessingHandler {

    private final OrderProcessingHandler delegate;

    public ValidatingOrderHandlerDecorator(BaseOrderProcessingHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public void handleSubmit(OrderProcessingContext context) {
        Order o = context.getOrder();
        if (o.getPatientName().isBlank()
                || o.getOrderingClinician().isBlank()
                || o.getDescription().isBlank()) {
            throw new IllegalArgumentException("Patient, clinician, and description are required.");
        }
        delegate.handleSubmit(context);
    }
}
