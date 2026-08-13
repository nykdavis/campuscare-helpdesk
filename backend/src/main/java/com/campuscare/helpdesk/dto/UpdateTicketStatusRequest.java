package com.campuscare.helpdesk.dto;

import com.campuscare.helpdesk.entity.TicketStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateTicketStatusRequest(
        @NotNull(message = "Status is required") TicketStatus status) {
}
