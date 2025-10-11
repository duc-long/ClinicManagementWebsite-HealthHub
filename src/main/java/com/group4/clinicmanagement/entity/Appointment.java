package com.group4.clinicmanagement.entity;

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
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "receptionist_id")
    private User receptionist;

    @Column(nullable = false)
    private LocalDate appointmentDate;

    private Integer status; // 0=pending,1=confirmed,...
    private String notes;
    private String cancelReason;
    private Integer queueNumber;
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentId=" + appointmentId +
                ", patient=" + patient +
                ", doctor=" + doctor +
                ", receptionist=" + receptionist +
                ", appointmentDate=" + appointmentDate +
                ", status=" + status +
                ", notes='" + notes + '\'' +
                ", cancelReason='" + cancelReason + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
