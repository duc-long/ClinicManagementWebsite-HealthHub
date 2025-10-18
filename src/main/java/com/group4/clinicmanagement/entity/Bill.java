package com.group4.clinicmanagement.entity;

import com.group4.clinicmanagement.enums.BillStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "Bill")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer billId;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "lab_request_id")
    private LabRequest labRequest;

    @ManyToOne
    @JoinColumn(name = "cashier_id", nullable = false)
    private User cashier;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "status")
    private Integer statusValue;

    @Transient
    private BillStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    @PostLoad
    public void loadEnum() {
        this.status = BillStatus.fromInt(this.statusValue != null ? this.statusValue : 0);
    }

    @PrePersist
    @PreUpdate
    public void persistEnumValue() {
        this.statusValue = (status != null) ? status.getValue() : 0;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
        this.statusValue = (status != null) ? status.getValue() : 0;
    }
}
