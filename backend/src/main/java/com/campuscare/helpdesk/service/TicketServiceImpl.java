package com.campuscare.helpdesk.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campuscare.helpdesk.dto.CreateTicketRequest;
import com.campuscare.helpdesk.dto.TicketResponse;
import com.campuscare.helpdesk.dto.UpdateTicketRequest;
import com.campuscare.helpdesk.dto.UpdateTicketStatusRequest;
import com.campuscare.helpdesk.entity.Ticket;
import com.campuscare.helpdesk.entity.TicketCategory;
import com.campuscare.helpdesk.entity.TicketStatus;
import com.campuscare.helpdesk.exception.ResourceNotFoundException;
import com.campuscare.helpdesk.repository.TicketRepository;

@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public TicketResponse createTicket(CreateTicketRequest request) {
        Ticket ticket = new Ticket(request.title(), request.description(), request.category(),
                request.studentName(), request.studentEmail());
        return toResponse(ticketRepository.save(ticket));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketResponse> getAllTickets(TicketStatus status, TicketCategory category, String studentEmail) {
        String email = studentEmail == null || studentEmail.isBlank() ? null : studentEmail.trim();
        List<Ticket> tickets;
        if (status != null && category != null && email != null) {
            tickets = ticketRepository.findByStatusAndCategoryAndStudentEmailIgnoreCaseOrderByCreatedAtDesc(
                    status, category, email);
        } else if (status != null && category != null) {
            tickets = ticketRepository.findByStatusAndCategoryOrderByCreatedAtDesc(status, category);
        } else if (status != null && email != null) {
            tickets = ticketRepository.findByStatusAndStudentEmailIgnoreCaseOrderByCreatedAtDesc(status, email);
        } else if (category != null && email != null) {
            tickets = ticketRepository.findByCategoryAndStudentEmailIgnoreCaseOrderByCreatedAtDesc(category, email);
        } else if (status != null) {
            tickets = ticketRepository.findByStatusOrderByCreatedAtDesc(status);
        } else if (category != null) {
            tickets = ticketRepository.findByCategoryOrderByCreatedAtDesc(category);
        } else if (email != null) {
            tickets = ticketRepository.findByStudentEmailIgnoreCaseOrderByCreatedAtDesc(email);
        } else {
            tickets = ticketRepository.findAllByOrderByCreatedAtDesc();
        }
        return tickets.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TicketResponse getTicketById(Long id) {
        return toResponse(findTicket(id));
    }

    @Override
    public TicketResponse updateTicket(Long id, UpdateTicketRequest request) {
        Ticket ticket = findTicket(id);
        ticket.updateDetails(request.title(), request.description(), request.category(),
                request.studentName(), request.studentEmail());
        return toResponse(ticketRepository.save(ticket));
    }

    @Override
    public TicketResponse updateTicketStatus(Long id, UpdateTicketStatusRequest request) {
        Ticket ticket = findTicket(id);
        ticket.updateStatus(request.status());
        return toResponse(ticketRepository.save(ticket));
    }

    @Override
    public void deleteTicket(Long id) {
        Ticket ticket = findTicket(id);
        ticketRepository.delete(ticket);
    }

    private Ticket findTicket(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
    }

    private TicketResponse toResponse(Ticket ticket) {
        return new TicketResponse(ticket.getId(), ticket.getTitle(), ticket.getDescription(),
                ticket.getCategory(), ticket.getStatus(), ticket.getStudentName(), ticket.getStudentEmail(),
                ticket.getCreatedAt(), ticket.getUpdatedAt());
    }
}
