package com.group4.clinicmanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Thực thể Bill ánh xạ từ bảng Bill trong database.
 */
@Entity
@Table(name = "Bill")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billId;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "cashier_id", nullable = false)
    private User cashier;

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "lab_request_id")
    private LabRequest labRequest;

    @Column(nullable = false)
    private BigDecimal amount;

    private String type; // Consultation, Lab Test, Medication, Follow-up

    private Integer status; // 0=pending, 1=paid, 2=void

    private LocalDateTime createdAt;

    private LocalDateTime paidAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
