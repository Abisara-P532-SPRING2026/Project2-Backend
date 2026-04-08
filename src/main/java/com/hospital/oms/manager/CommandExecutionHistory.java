package com.hospital.oms.manager;

import com.hospital.oms.command.OrderCommand;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

@Component
public class CommandExecutionHistory {

    private final Deque<OrderCommand> stack = new ArrayDeque<>();
    private final List<OrderCommand> timeline = new ArrayList<>();

    public synchronized void push(OrderCommand command) {
        stack.clear();
        stack.push(command);
        timeline.add(command);
    }

    public synchronized Optional<OrderCommand> popLast() {
        return Optional.ofNullable(stack.pollFirst());
    }

    public synchronized Optional<OrderCommand> peekLast() {
        return Optional.ofNullable(stack.peekFirst());
    }

    public synchronized Optional<OrderCommand> byNewestIndex(int newestFirstIndex) {
        if (newestFirstIndex < 0 || newestFirstIndex >= timeline.size()) {
            return Optional.empty();
        }
        int idx = timeline.size() - 1 - newestFirstIndex;
        return Optional.ofNullable(timeline.get(idx));
    }
}
