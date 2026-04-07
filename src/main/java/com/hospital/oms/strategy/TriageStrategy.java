package com.hospital.oms.strategy;

import com.hospital.oms.domain.Order;

import java.util.List;

public interface TriageStrategy {

    List<Order> sortPending(List<Order> pending, List<Order> inProgress);
}
