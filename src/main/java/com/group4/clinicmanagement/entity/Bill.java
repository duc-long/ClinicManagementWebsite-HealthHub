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
    @Column(name = "bill_id")
    private Integer billId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = true)
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashier_id", nullable = false)
    private Staff cashier;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(name = "status", nullable = false)
    private Integer statusValue;

    @Transient
    private BillStatus status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "notes", length = 1000)
    private String notes;

    @PostLoad
    private void loadEnum() {
        if (this.statusValue != null) {
            this.status = BillStatus.fromInt(this.statusValue);
        }
    }

    @PrePersist
    @PreUpdate
    private void persistEnumValue() {
        if (this.status != null) {
            this.statusValue = this.status.getValue();
        }
    }

    // Custom setter to keep sync
    public void setStatus(BillStatus status) {
        this.status = status;
        this.statusValue = (status != null) ? status.getValue() : 0;
    }

}