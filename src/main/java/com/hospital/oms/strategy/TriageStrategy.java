package com.hospital.oms.strategy;

import com.hospital.oms.domain.Order;

/**
 * Strategy pattern: pluggable triage / queue ordering policy.
 */
public interface TriageStrategy {

    /**
     * Negative if {@code a} should come before {@code b} in the queue (higher urgency first).
     */
    int compareQueuePosition(Order a, Order b);
}
