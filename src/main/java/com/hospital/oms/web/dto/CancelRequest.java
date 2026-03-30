package com.hospital.oms.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CancelRequest(@NotBlank String actor) {}
