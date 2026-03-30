package com.hospital.oms.web.dto;

import com.hospital.oms.commandlog.CommandLogEntry;

import java.time.Instant;

public record CommandLogResponse(Instant timestamp, String commandType, String orderId, String actor) {

    public static CommandLogResponse from(CommandLogEntry e) {
        return new CommandLogResponse(e.timestamp(), e.commandType(), e.orderId(), e.actor());
    }
}
