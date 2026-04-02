package com.hospital.oms.web.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

/** JavaBean so {@code clinicianId} and legacy {@code actor} can appear in the same JSON safely. */
public class CancelRequest {

    @NotBlank
    @JsonAlias("actor")
    private String clinicianId;

    public CancelRequest() {}

    public CancelRequest(String clinicianId) {
        this.clinicianId = clinicianId;
    }

    public String getClinicianId() {
        return clinicianId;
    }

    public void setClinicianId(String clinicianId) {
        this.clinicianId = clinicianId;
    }
}
