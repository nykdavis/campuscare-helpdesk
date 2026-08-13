package com.campuscare.helpdesk.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campuscare.helpdesk.dto.CreateTicketRequest;
import com.campuscare.helpdesk.dto.TicketResponse;
import com.campuscare.helpdesk.dto.UpdateTicketRequest;
import com.campuscare.helpdesk.dto.UpdateTicketStatusRequest;
import com.campuscare.helpdesk.entity.TicketCategory;
import com.campuscare.helpdesk.entity.TicketStatus;
import com.campuscare.helpdesk.service.TicketService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody CreateTicketRequest request) {
        TicketResponse response = ticketService.createTicket(request);
        return ResponseEntity.created(URI.create("/api/tickets/" + response.id())).body(response);
    }

    @GetMapping
    public List<TicketResponse> getAllTickets(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketCategory category,
            @RequestParam(required = false) String studentEmail) {
        return ticketService.getAllTickets(status, category, studentEmail);
    }

    @GetMapping("/{id}")
    public TicketResponse getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id);
    }

    @PutMapping("/{id}")
    public TicketResponse updateTicket(@PathVariable Long id,
                                       @Valid @RequestBody UpdateTicketRequest request) {
        return ticketService.updateTicket(id, request);
    }

    @PatchMapping("/{id}/status")
    public TicketResponse updateTicketStatus(@PathVariable Long id,
                                             @Valid @RequestBody UpdateTicketStatusRequest request) {
        return ticketService.updateTicketStatus(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}
