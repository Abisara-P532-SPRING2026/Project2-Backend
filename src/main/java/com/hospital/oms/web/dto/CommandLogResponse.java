package com.hospital.oms.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hospital.oms.commandlog.CommandLogEntry;

import java.time.Instant;

public record CommandLogResponse(Instant timestamp, String commandType, String orderId, String performedBy) {

    public static CommandLogResponse from(CommandLogEntry e) {
        String who = e.performedBy();
        if (who == null) {
            who = "";
        }
        return new CommandLogResponse(e.timestamp(), e.commandType(), e.orderId(), who);
    }

    @JsonProperty("actor")
    public String actor() {
        return performedBy;
    }
}
