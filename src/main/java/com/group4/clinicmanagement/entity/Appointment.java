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
    private Integer appointmentId;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = true)
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "receptionist_id")
    private User receptionist;

    @Column(nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "status", nullable = false)
    private Integer statusValue;

    @Transient
    private AppointmentStatus status;

    private String notes;
    private String cancelReason;

    @OneToOne(mappedBy = "appointment", fetch = FetchType.LAZY)
    private MedicalRecord medicalRecord;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name="queue_number")
    private Integer queueNumber;
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


}
