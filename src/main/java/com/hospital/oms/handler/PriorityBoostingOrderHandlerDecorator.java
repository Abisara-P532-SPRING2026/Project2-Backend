package com.hospital.oms.handler;

import com.hospital.oms.domain.Priority;

public class PriorityBoostingOrderHandlerDecorator implements OrderProcessingHandler {

    private final OrderProcessingHandler delegate;

    public PriorityBoostingOrderHandlerDecorator(OrderProcessingHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public void handleSubmit(OrderProcessingContext context) {
        if (context.getOrder().getPriority() == Priority.ROUTINE) {
            // Placeholder: future policy could promote based on clinical rules
        }
        delegate.handleSubmit(context);
    }
}
