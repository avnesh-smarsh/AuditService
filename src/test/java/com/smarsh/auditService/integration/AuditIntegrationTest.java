package com.smarsh.auditService.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarsh.auditService.dto.AuditLogRequest;
import com.smarsh.auditService.model.AuditLog;
import com.smarsh.auditService.repository.AuditRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuditIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuditRepository auditRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanup() {
        // Clean up before each test
        auditRepository.deleteAll();
    }

    @Test
    void createAndRetrieveAuditLog_IntegrationTest() throws Exception {
        // Create audit log
        AuditLogRequest request = AuditLogRequest.builder()
                .messageId("test-message-1")
                .network("email")
                .eventType("PROCESSED")
                .service("integration-test")
                .timestamp(Instant.now())
                .details(Map.of("test", "data"))
                .build();

        MvcResult result = mockMvc.perform(post("/api/audit/tenant-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageId").value("test-message-1"))
                .andExpect(jsonPath("$.tenantId").value("tenant-1"))
                .andExpect(jsonPath("$.auditId").exists())
                .andReturn();

        // Verify the response can be parsed
        String responseContent = result.getResponse().getContentAsString();
        AuditLog responseLog = objectMapper.readValue(responseContent, AuditLog.class);
        assertNotNull(responseLog.getAuditId());

        // Retrieve audit logs
        mockMvc.perform(get("/api/audit/tenant-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].messageId").value("test-message-1"))
                .andExpect(jsonPath("$[0].tenantId").value("tenant-1"));
    }

    @Test
    void createAuditLog_ValidationFailure_MissingMessageId() throws Exception {
        AuditLogRequest invalidRequest = AuditLogRequest.builder()
                .messageId(null) // Missing required field
                .network("email")
                .eventType("PROCESSED")
                .service("test-service")
                .timestamp(Instant.now())
                .details(Map.of("key", "value"))
                .build();

        mockMvc.perform(post("/api/audit/tenant-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Message ID is required")));
    }

    @Test
    void createAuditLog_ValidationFailure_InvalidNetwork() throws Exception {
        AuditLogRequest invalidRequest = AuditLogRequest.builder()
                .messageId("test-message")
                .network("invalid-network") // Invalid network
                .eventType("PROCESSED")
                .service("test-service")
                .timestamp(Instant.now())
                .details(Map.of("key", "value"))
                .build();

        mockMvc.perform(post("/api/audit/tenant-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Network must be either 'email' or 'slack'")));
    }

    @Test
    void createAuditLog_ValidationFailure_InvalidEventType() throws Exception {
        AuditLogRequest invalidRequest = AuditLogRequest.builder()
                .messageId("test-message")
                .network("email")
                .eventType("INVALID_EVENT") // Invalid event type
                .service("test-service")
                .timestamp(Instant.now())
                .details(Map.of("key", "value"))
                .build();

        mockMvc.perform(post("/api/audit/tenant-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Invalid event type")));
    }

    @Test
    void getAuditById_Success() throws Exception {
        // First create an audit log
        AuditLogRequest request = AuditLogRequest.builder()
                .messageId("test-message-2")
                .network("email")
                .eventType("PROCESSED")
                .service("integration-test")
                .timestamp(Instant.now())
                .details(Map.of("test", "data"))
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/audit/tenant-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        // Extract the audit ID from the response
        String responseContent = createResult.getResponse().getContentAsString();
        AuditLog createdLog = objectMapper.readValue(responseContent, AuditLog.class);
        String auditId = createdLog.getAuditId();

        // Then retrieve it by ID
        mockMvc.perform(get("/api/audit/tenant-1/{auditId}", auditId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.auditId").value(auditId))
                .andExpect(jsonPath("$.messageId").value("test-message-2"));
    }

    @Test
    void getAuditById_NotFound() throws Exception {
        mockMvc.perform(get("/api/audit/tenant-1/nonexistent-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Audit log not found")));
    }

    @Test
    void getAuditsByMessageId_Success() throws Exception {
        // Create multiple logs with same message ID
        AuditLogRequest request = AuditLogRequest.builder()
                .messageId("same-message")
                .network("email")
                .eventType("PROCESSED")
                .service("integration-test")
                .timestamp(Instant.now())
                .details(Map.of("test", "data"))
                .build();

        // Create first log
        mockMvc.perform(post("/api/audit/tenant-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Create second log with same message ID
        mockMvc.perform(post("/api/audit/tenant-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Retrieve by message ID
        mockMvc.perform(get("/api/audit/tenant-1/message/same-message"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].messageId").value("same-message"))
                .andExpect(jsonPath("$[1].messageId").value("same-message"));
    }
}