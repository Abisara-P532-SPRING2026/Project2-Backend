package com.hospital.oms.commandlog;

import java.time.Instant;

public record CommandLogEntry(Instant timestamp, String commandType, String orderId, String actor) {}
