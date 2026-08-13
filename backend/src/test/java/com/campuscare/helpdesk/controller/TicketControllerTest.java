package com.campuscare.helpdesk.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.campuscare.helpdesk.dto.CreateTicketRequest;
import com.campuscare.helpdesk.dto.TicketResponse;
import com.campuscare.helpdesk.dto.UpdateTicketStatusRequest;
import com.campuscare.helpdesk.entity.TicketCategory;
import com.campuscare.helpdesk.entity.TicketStatus;
import com.campuscare.helpdesk.exception.GlobalExceptionHandler;
import com.campuscare.helpdesk.exception.ResourceNotFoundException;
import com.campuscare.helpdesk.service.TicketService;

@ExtendWith(MockitoExtension.class)
class TicketControllerTest {

    @Mock
    private TicketService ticketService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TicketController(ticketService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createsTicketSuccessfully() throws Exception {
        when(ticketService.createTicket(any(CreateTicketRequest.class))).thenReturn(ticket(TicketStatus.OPEN));

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreateJson()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/tickets/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void rejectsMissingRequiredFields() throws Exception {
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.validationErrors.title").value("Title is required"))
                .andExpect(jsonPath("$.validationErrors.category").value("Category is required"));
    }

    @Test
    void fetchesTicketById() throws Exception {
        when(ticketService.getTicketById(1L)).thenReturn(ticket(TicketStatus.OPEN));

        mockMvc.perform(get("/api/tickets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Unable to access campus Wi-Fi"));
    }

    @Test
    void returnsNotFoundForUnknownTicket() throws Exception {
        when(ticketService.getTicketById(99L))
                .thenThrow(new ResourceNotFoundException("Ticket not found with id: 99"));

        mockMvc.perform(get("/api/tickets/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Ticket not found with id: 99"));
    }

    @Test
    void updatesTicketStatus() throws Exception {
        when(ticketService.updateTicketStatus(any(Long.class), any(UpdateTicketStatusRequest.class)))
                .thenReturn(ticket(TicketStatus.IN_PROGRESS));

        mockMvc.perform(patch("/api/tickets/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"IN_PROGRESS\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void deletesTicket() throws Exception {
        doNothing().when(ticketService).deleteTicket(1L);

        mockMvc.perform(delete("/api/tickets/1"))
                .andExpect(status().isNoContent());

        verify(ticketService).deleteTicket(1L);
    }

    private TicketResponse ticket(TicketStatus status) {
        LocalDateTime timestamp = LocalDateTime.of(2026, 8, 14, 10, 30);
        return new TicketResponse(1L, "Unable to access campus Wi-Fi",
                "The Wi-Fi login page does not accept my student account.",
                TicketCategory.IT_SUPPORT, status, "Asha Rao", "asha.rao@example.com",
                timestamp, timestamp);
    }

    private String validCreateJson() {
        return """
                {
                  "title": "Unable to access campus Wi-Fi",
                  "description": "The Wi-Fi login page does not accept my student account.",
                  "category": "IT_SUPPORT",
                  "studentName": "Asha Rao",
                  "studentEmail": "asha.rao@example.com"
                }
                """;
    }
}
