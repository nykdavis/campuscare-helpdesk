package com.campuscare.helpdesk.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campuscare.helpdesk.dto.CreateTicketRequest;
import com.campuscare.helpdesk.dto.TicketResponse;
import com.campuscare.helpdesk.dto.UpdateTicketRequest;
import com.campuscare.helpdesk.dto.UpdateTicketStatusRequest;
import com.campuscare.helpdesk.entity.AppUser;
import com.campuscare.helpdesk.entity.Ticket;
import com.campuscare.helpdesk.entity.TicketCategory;
import com.campuscare.helpdesk.entity.TicketStatus;
import com.campuscare.helpdesk.exception.ResourceNotFoundException;
import com.campuscare.helpdesk.repository.TicketRepository;
import com.campuscare.helpdesk.security.CurrentUserService;

@Service
@Transactional
public class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepository;
    private final CurrentUserService currentUserService;

    public TicketServiceImpl(TicketRepository ticketRepository, CurrentUserService currentUserService) {
        this.ticketRepository = ticketRepository;
        this.currentUserService = currentUserService;
    }

    @Override
    public TicketResponse createTicket(CreateTicketRequest request) {
        AppUser user = currentUserService.getCurrentUser();
        Ticket ticket = new Ticket(request.title(), request.description(), request.category(),
                user.getName(), user.getEmail());
        return toResponse(ticketRepository.save(ticket));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketResponse> getAllTickets(TicketStatus status, TicketCategory category, String studentEmail) {
        AppUser user = currentUserService.getCurrentUser();
        String email = currentUserService.isAdmin(user) ? normalized(studentEmail) : user.getEmail();
        List<Ticket> tickets;
        if (status != null && category != null && email != null) {
            tickets = ticketRepository.findByStatusAndCategoryAndStudentEmailIgnoreCaseOrderByCreatedAtDesc(status, category, email);
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
        AppUser user = currentUserService.getCurrentUser();
        Ticket ticket = findTicket(id);
        requireOwnerOrAdmin(ticket, user);
        return toResponse(ticket);
    }

    @Override
    public TicketResponse updateTicket(Long id, UpdateTicketRequest request) {
        AppUser user = currentUserService.getCurrentUser();
        Ticket ticket = findTicket(id);
        requireOwnerOrAdmin(ticket, user);
        String name = currentUserService.isAdmin(user) ? request.studentName() : user.getName();
        String email = currentUserService.isAdmin(user) ? request.studentEmail() : user.getEmail();
        ticket.updateDetails(request.title(), request.description(), request.category(), name, email);
        return toResponse(ticketRepository.save(ticket));
    }

    @Override
    public TicketResponse updateTicketStatus(Long id, UpdateTicketStatusRequest request) {
        AppUser user = currentUserService.getCurrentUser();
        requireAdmin(user);
        Ticket ticket = findTicket(id);
        ticket.updateStatus(request.status());
        return toResponse(ticketRepository.save(ticket));
    }

    @Override
    public void deleteTicket(Long id) {
        requireAdmin(currentUserService.getCurrentUser());
        ticketRepository.delete(findTicket(id));
    }

    private Ticket findTicket(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
    }

    private void requireOwnerOrAdmin(Ticket ticket, AppUser user) {
        if (!currentUserService.isAdmin(user) && !ticket.getStudentEmail().equalsIgnoreCase(user.getEmail())) {
            throw new AccessDeniedException("You do not have permission to access this ticket");
        }
    }

    private void requireAdmin(AppUser user) {
        if (!currentUserService.isAdmin(user)) {
            throw new AccessDeniedException("Administrator access is required");
        }
    }

    private String normalized(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private TicketResponse toResponse(Ticket ticket) {
        return new TicketResponse(ticket.getId(), ticket.getTitle(), ticket.getDescription(),
                ticket.getCategory(), ticket.getStatus(), ticket.getStudentName(), ticket.getStudentEmail(),
                ticket.getCreatedAt(), ticket.getUpdatedAt());
    }
}
