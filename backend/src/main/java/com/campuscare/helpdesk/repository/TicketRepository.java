package com.campuscare.helpdesk.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campuscare.helpdesk.entity.Ticket;
import com.campuscare.helpdesk.entity.TicketCategory;
import com.campuscare.helpdesk.entity.TicketStatus;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByOrderByCreatedAtDesc();

    List<Ticket> findByStatusOrderByCreatedAtDesc(TicketStatus status);

    List<Ticket> findByCategoryOrderByCreatedAtDesc(TicketCategory category);

    List<Ticket> findByStudentEmailIgnoreCaseOrderByCreatedAtDesc(String studentEmail);

    List<Ticket> findByStatusAndCategoryAndStudentEmailIgnoreCaseOrderByCreatedAtDesc(
            TicketStatus status, TicketCategory category, String studentEmail);

    List<Ticket> findByStatusAndCategoryOrderByCreatedAtDesc(TicketStatus status, TicketCategory category);

    List<Ticket> findByStatusAndStudentEmailIgnoreCaseOrderByCreatedAtDesc(
            TicketStatus status, String studentEmail);

    List<Ticket> findByCategoryAndStudentEmailIgnoreCaseOrderByCreatedAtDesc(
            TicketCategory category, String studentEmail);
}
