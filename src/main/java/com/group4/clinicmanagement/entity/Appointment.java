package com.group4.clinicmanagement.entity;

import com.group4.clinicmanagement.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Integer appointmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receptionist_id")
    private Staff receptionist;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "status", nullable = false)
    private Integer statusValue;

    @Transient
    private AppointmentStatus status;

    @Column(name = "cancel_reason", length = 500)
    private String cancelReason;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "queue_number")
    private Integer queueNumber;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // === 1-1 ===
    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private MedicalRecord medicalRecord;

    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Feedback feedback;

    @PostLoad
    public void fillStatusEnum() {
        this.status = AppointmentStatus.fromInt(this.statusValue);
    }

    @PrePersist
    @PreUpdate
    public void fillStatusValue() {
        if (this.status != null) {
            this.statusValue = this.status.getValue();
        }
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
        this.statusValue = (status != null) ? status.getValue() : null;
    }
}