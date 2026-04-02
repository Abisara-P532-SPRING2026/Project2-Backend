package com.hospital.oms.web.dto;

import com.hospital.oms.commandlog.CommandLogEntry;

import java.time.Instant;

/**
 * Audit row for the API. Use {@code performedBy} only here — Jackson + records break if an extra
 * {@code @JsonProperty("actor")} accessor is added for the same value.
 */
public record CommandLogResponse(Instant timestamp, String commandType, String orderId, String performedBy) {

    public static CommandLogResponse from(CommandLogEntry e) {
        String who = e.performedBy();
        if (who == null) {
            who = "";
        }
        return new CommandLogResponse(e.timestamp(), e.commandType(), e.orderId(), who);
    }
}
