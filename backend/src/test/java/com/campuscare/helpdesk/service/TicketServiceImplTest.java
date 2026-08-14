package com.campuscare.helpdesk.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.campuscare.helpdesk.dto.CreateTicketRequest;
import com.campuscare.helpdesk.entity.AppUser;
import com.campuscare.helpdesk.entity.Ticket;
import com.campuscare.helpdesk.entity.TicketCategory;
import com.campuscare.helpdesk.entity.TicketStatus;
import com.campuscare.helpdesk.entity.UserRole;
import com.campuscare.helpdesk.exception.ResourceNotFoundException;
import com.campuscare.helpdesk.repository.TicketRepository;
import com.campuscare.helpdesk.security.CurrentUserService;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {
    @Mock TicketRepository ticketRepository;
    @Mock CurrentUserService currentUserService;
    @InjectMocks TicketServiceImpl ticketService;

    @Test
    void newTicketUsesAuthenticatedStudentAndStartsOpen() {
        AppUser student = student();
        when(currentUserService.getCurrentUser()).thenReturn(student);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));
        CreateTicketRequest request = new CreateTicketRequest("Wi-Fi issue", "Cannot connect",
                TicketCategory.IT_SUPPORT, "Someone Else", "other@example.com");

        ticketService.createTicket(request);

        ArgumentCaptor<Ticket> captor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepository).save(captor.capture());
        assertEquals(TicketStatus.OPEN, captor.getValue().getStatus());
        assertEquals("asha.rao@example.com", captor.getValue().getStudentEmail());
    }

    @Test
    void studentCannotReadAnotherStudentsTicket() {
        AppUser student = student();
        Ticket otherTicket = new Ticket("Issue", "Description", TicketCategory.OTHER,
                "Other Student", "other@example.com");
        when(currentUserService.getCurrentUser()).thenReturn(student);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(otherTicket));

        assertThrows(AccessDeniedException.class, () -> ticketService.getTicketById(1L));
    }

    @Test
    void unknownTicketThrowsResourceNotFound() {
        when(currentUserService.getCurrentUser()).thenReturn(student());
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> ticketService.getTicketById(99L));
        assertEquals("Ticket not found with id: 99", exception.getMessage());
    }

    private AppUser student() {
        return new AppUser("Asha Rao", "asha.rao@example.com", "encoded", UserRole.STUDENT);
    }
}
