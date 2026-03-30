package com.hospital.oms.config;

import com.hospital.oms.handler.AuditLoggingOrderHandlerDecorator;
import com.hospital.oms.handler.BaseOrderProcessingHandler;
import com.hospital.oms.handler.OrderProcessingHandler;
import com.hospital.oms.handler.PriorityBoostingOrderHandlerDecorator;
import com.hospital.oms.handler.ValidatingOrderHandlerDecorator;
import com.hospital.oms.resourceaccess.OrderAccess;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderProcessingConfig {

    @Bean
    public OrderProcessingHandler orderSubmissionPipeline(OrderAccess orderAccess) {
        BaseOrderProcessingHandler base = new BaseOrderProcessingHandler(orderAccess);
        ValidatingOrderHandlerDecorator validating = new ValidatingOrderHandlerDecorator(base);
        PriorityBoostingOrderHandlerDecorator boosting = new PriorityBoostingOrderHandlerDecorator(validating);
        return new AuditLoggingOrderHandlerDecorator(boosting);
    }
}
