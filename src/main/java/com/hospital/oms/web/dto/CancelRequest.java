package com.hospital.oms.web.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public record CancelRequest(@NotBlank @JsonAlias("actor") String clinicianId) {}
