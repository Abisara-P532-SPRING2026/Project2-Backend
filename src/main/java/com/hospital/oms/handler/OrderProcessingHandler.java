package com.hospital.oms.handler;

/**
 * Decorator pattern: core handler interface for stacked submission processing.
 */
public interface OrderProcessingHandler {

    void handleSubmit(OrderProcessingContext context);
}
