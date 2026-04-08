package com.hospital.oms.handler;

import com.hospital.oms.domain.Priority;


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
        if (context.getOrder().getPriority() == Priority.STAT) {
            long affectedPatientCount =
                    context.getOrderStorageEngine().listPendingOrders().stream()
                            .filter(o -> o.getType() == context.getOrder().getType())
                            .map(o -> o.getPatientName().trim().toLowerCase())
                            .distinct()
                            .count();
            context.addAuditDetail("affectedPatientCount", String.valueOf(affectedPatientCount));
            if (!context.getAuditDetails().containsKey("escalatedToStat")) {
                context.addAuditDetail("escalatedToStat", "false");
                context.addAuditDetail("escalationReason", "submittedAsStat");
            }
        }
        System.out.printf(
                "[PIPELINE_AUDIT] after persist orderId=%s%n", context.getOrder().getId());
    }
}
