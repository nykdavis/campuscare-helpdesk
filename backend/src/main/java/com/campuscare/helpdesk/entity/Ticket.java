package com.campuscare.helpdesk.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String title;

    @NotBlank
    @Size(max = 2000)
    @Column(nullable = false, length = 2000)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TicketCategory category;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TicketStatus status = TicketStatus.OPEN;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String studentName;

    @NotBlank
    @Email
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String studentEmail;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected Ticket() {
    }

    public Ticket(String title, String description, TicketCategory category,
                  String studentName, String studentEmail) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.status = TicketStatus.OPEN;
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) {
            status = TicketStatus.OPEN;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TicketCategory getCategory() { return category; }
    public TicketStatus getStatus() { return status; }
    public String getStudentName() { return studentName; }
    public String getStudentEmail() { return studentEmail; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void updateDetails(String title, String description, TicketCategory category,
                              String studentName, String studentEmail) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
    }

    public void updateStatus(TicketStatus status) {
        this.status = status;
    }
}
