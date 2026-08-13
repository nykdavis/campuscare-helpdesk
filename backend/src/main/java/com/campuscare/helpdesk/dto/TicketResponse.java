package com.campuscare.helpdesk.dto;

import java.time.LocalDateTime;

import com.campuscare.helpdesk.entity.TicketCategory;
import com.campuscare.helpdesk.entity.TicketStatus;

public record TicketResponse(
        Long id,
        String title,
        String description,
        TicketCategory category,
        TicketStatus status,
        String studentName,
        String studentEmail,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
