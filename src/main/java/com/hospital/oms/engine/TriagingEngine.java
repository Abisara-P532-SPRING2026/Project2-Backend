package com.hospital.oms.engine;

import com.hospital.oms.domain.Order;
import com.hospital.oms.strategy.TriageStrategy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Component
public class TriagingEngine {

    private final TriageStrategy triageStrategy;

    public TriagingEngine(TriageStrategy triageStrategy) {
        this.triageStrategy = triageStrategy;
    }

    /** Returns a sorted copy; does not load from storage. */
    public List<Order> sortPendingQueue(List<Order> pending) {
        List<Order> copy = new ArrayList<>(pending);
        Comparator<Order> cmp = triageStrategy::compareQueuePosition;
        copy.sort(cmp);
        return copy;
    }
}
