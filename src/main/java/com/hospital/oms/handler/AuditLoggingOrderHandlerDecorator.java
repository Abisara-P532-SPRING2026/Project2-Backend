package com.hospital.oms.handler;


public class AuditLoggingOrderHandlerDecorator implements OrderProcessingHandler {

    private final OrderProcessingHandler delegate;

    public AuditLoggingOrderHandlerDecorator(OrderProcessingHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public void handleSubmit(OrderProcessingContext context) {
        System.out.printf(
                "[PIPELINE_AUDIT] before persist orderId=%s%n", context.getOrder().getId());
        delegate.handleSubmit(context);
        System.out.printf(
                "[PIPELINE_AUDIT] after persist orderId=%s%n", context.getOrder().getId());
    }
}
