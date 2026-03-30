package com.hospital.oms.commandlog;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class InMemoryCommandLog {

    private final List<CommandLogEntry> entries = Collections.synchronizedList(new ArrayList<>());

    public void append(CommandLogEntry entry) {
        entries.add(entry);
    }

    public List<CommandLogEntry> getEntriesNewestFirst() {
        synchronized (entries) {
            List<CommandLogEntry> copy = new ArrayList<>(entries);
            Collections.reverse(copy);
            return copy;
        }
    }
}
