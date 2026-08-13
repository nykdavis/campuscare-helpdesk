package com.campuscare.helpdesk.service;

import java.util.List;

import com.campuscare.helpdesk.dto.CreateTicketRequest;
import com.campuscare.helpdesk.dto.TicketResponse;
import com.campuscare.helpdesk.dto.UpdateTicketRequest;
import com.campuscare.helpdesk.dto.UpdateTicketStatusRequest;
import com.campuscare.helpdesk.entity.TicketCategory;
import com.campuscare.helpdesk.entity.TicketStatus;

public interface TicketService {

    TicketResponse createTicket(CreateTicketRequest request);

    List<TicketResponse> getAllTickets(TicketStatus status, TicketCategory category, String studentEmail);

    TicketResponse getTicketById(Long id);

    TicketResponse updateTicket(Long id, UpdateTicketRequest request);

    TicketResponse updateTicketStatus(Long id, UpdateTicketStatusRequest request);

    void deleteTicket(Long id);
}
