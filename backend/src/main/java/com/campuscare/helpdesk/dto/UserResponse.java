package com.campuscare.helpdesk.dto;

import com.campuscare.helpdesk.entity.UserRole;

public record UserResponse(Long id, String name, String email, UserRole role) {
}
