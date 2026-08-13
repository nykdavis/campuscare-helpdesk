package com.campuscare.helpdesk.dto;

import com.campuscare.helpdesk.entity.TicketCategory;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateTicketRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 120, message = "Title must not exceed 120 characters")
        String title,
        @NotBlank(message = "Description is required")
        @Size(max = 2000, message = "Description must not exceed 2000 characters")
        String description,
        @NotNull(message = "Category is required")
        TicketCategory category,
        @NotBlank(message = "Student name is required")
        @Size(max = 100, message = "Student name must not exceed 100 characters")
        String studentName,
        @NotBlank(message = "Student email is required")
        @Email(message = "Student email must be valid")
        @Size(max = 150, message = "Student email must not exceed 150 characters")
        String studentEmail) {
}
