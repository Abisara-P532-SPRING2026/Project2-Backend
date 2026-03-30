package com.hospital.oms.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.oms.domain.OrderType;
import com.hospital.oms.domain.Priority;
import com.hospital.oms.web.dto.SubmitOrderRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void submitAndReadQueue() throws Exception {
        SubmitOrderRequest body =
                new SubmitOrderRequest(
                        OrderType.LAB, "Jane", "Dr. Who", "CBC", Priority.URGENT, "dr.who");
        mockMvc.perform(
                        post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientName").value("Jane"));

        mockMvc.perform(get("/api/orders/pending-queue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].priority").value("URGENT"));
    }

    @Test
    void auditListsCommands() throws Exception {
        SubmitOrderRequest body =
                new SubmitOrderRequest(
                        OrderType.MEDICATION, "A", "B", "Rx", Priority.ROUTINE, "nurse1");
        mockMvc.perform(
                post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)));

        mockMvc.perform(get("/api/audit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].commandType").value("SUBMIT"));
    }
}
