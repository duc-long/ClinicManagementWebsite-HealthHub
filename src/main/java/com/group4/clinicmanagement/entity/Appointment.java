package com.group4.clinicmanagement.entity;

import com.group4.clinicmanagement.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Appointment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Integer appointmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY) // SỬA: từ EAGER → LAZY
    @JoinColumn(name = "doctor_id", nullable = true)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receptionist_id", nullable = true)
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

    // One Appointment → One MedicalRecord
    @OneToOne(mappedBy = "appointment", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private MedicalRecord medicalRecord;

    // One Appointment → One Feedback
    @OneToOne(mappedBy = "appointment", fetch = FetchType.LAZY)
    private Feedback feedback; // SỬA: đổi tên thành số ít

    // XÓA HOÀN TOÀN quan hệ Bill (Bill đã có appointment)

    @PostLoad
    private void fillStatusEnum() {
        if (this.statusValue != null) {
            this.status = AppointmentStatus.fromInt(this.statusValue);
        }
    }

    @PrePersist
    @PreUpdate
    private void fillStatusValue() {
        if (this.status != null) {
            this.statusValue = this.status.getValue();
        }
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
        this.statusValue = (status != null) ? status.getValue() : null;
    }
}