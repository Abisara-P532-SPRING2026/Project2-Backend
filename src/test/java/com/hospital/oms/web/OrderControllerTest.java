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

import java.util.Map;

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
                        OrderType.LAB, "Jane", "dr.who", "CBC", Priority.URGENT, "Dr. Who");
        mockMvc.perform(
                        post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientName").value("Jane"))
                .andExpect(jsonPath("$.clinicianName").value("Dr. Who"));

        mockMvc.perform(get("/api/orders/pending-queue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].priority").value("URGENT"));
    }

    @Test
    void cancelRejectsNonPending() throws Exception {
        SubmitOrderRequest body =
                new SubmitOrderRequest(
                        OrderType.LAB, "Pat", "dr.real", "Labs", Priority.ROUTINE, "Dr. Real");
        String json =
                mockMvc.perform(
                                post("/api/orders")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(body)))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        String orderId = objectMapper.readTree(json).get("id").asText();

        mockMvc.perform(
                        post("/api/orders/" + orderId + "/claim")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("staffId", "staff1"))))
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/api/orders/" + orderId + "/cancel")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("clinicianId", "dr.real"))))
                .andExpect(status().isConflict());
    }

    @Test
    void cancelRejectsWrongClinician() throws Exception {
        SubmitOrderRequest body =
                new SubmitOrderRequest(
                        OrderType.LAB, "Pat", "owner.id", "Labs", Priority.ROUTINE, "Dr. Owner");
        String json =
                mockMvc.perform(
                                post("/api/orders")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(body)))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        String orderId = objectMapper.readTree(json).get("id").asText();

        mockMvc.perform(
                        post("/api/orders/" + orderId + "/cancel")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("clinicianId", "wrong.id"))))
                .andExpect(status().isConflict());
    }

    @Test
    void auditListsCommands() throws Exception {
        SubmitOrderRequest body =
                new SubmitOrderRequest(
                        OrderType.MEDICATION, "A", "nurse1", "Rx", Priority.ROUTINE, "Nurse One");
        mockMvc.perform(
                post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)));

        mockMvc.perform(get("/api/audit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].commandType").value("SUBMIT"));
    }

    @Test
    void updatesDepartmentTriageStrategyAtRuntime() throws Exception {
        mockMvc.perform(
                        post("/api/triage/departments/LAB")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("strategy", "DEADLINE_FIRST"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.department").value("LAB"))
                .andExpect(jsonPath("$.strategy").value("DEADLINE_FIRST"));

        mockMvc.perform(get("/api/triage/departments/LAB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.department").value("LAB"))
                .andExpect(jsonPath("$.strategy").value("DEADLINE_FIRST"));
    }

    @Test
    void adminUndoRevertsMostRecentCommand() throws Exception {
        SubmitOrderRequest body =
                new SubmitOrderRequest(
                        OrderType.LAB, "Undo Pat", "undo.dr", "CBC", Priority.URGENT, "Dr Undo");
        String submittedJson =
                mockMvc.perform(
                                post("/api/orders")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(body)))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        String orderId = objectMapper.readTree(submittedJson).get("id").asText();

        mockMvc.perform(
                        post("/api/orders/" + orderId + "/claim")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("staffId", "staff1"))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/admin/undo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));

        mockMvc.perform(get("/api/orders/pending-queue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(orderId))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void adminReplayByAuditIndexReexecutesCommand() throws Exception {
        SubmitOrderRequest body =
                new SubmitOrderRequest(
                        OrderType.LAB, "Replay Pat", "replay.dr", "CMP", Priority.ROUTINE, "Dr Replay");
        String submittedJson =
                mockMvc.perform(
                                post("/api/orders")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(body)))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        String orderId = objectMapper.readTree(submittedJson).get("id").asText();

        mockMvc.perform(
                        post("/api/orders/" + orderId + "/cancel")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("clinicianId", "replay.dr"))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/admin/replay/0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.auditIndex").value("0"));

        mockMvc.perform(get("/api/orders/pending-queue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(orderId))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }
}
