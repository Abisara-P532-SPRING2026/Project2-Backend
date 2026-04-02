package com.hospital.oms.strategy;

import com.hospital.oms.domain.Order;


public interface TriageStrategy {

    int compareQueuePosition(Order a, Order b);
}
