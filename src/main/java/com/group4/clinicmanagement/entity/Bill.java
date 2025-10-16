package com.group4.clinicmanagement.entity;

import com.group4.clinicmanagement.enums.BillStatus;
import com.group4.clinicmanagement.enums.BillType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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

    // ===== Relationships =====
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

    // ===== Fields =====
    @Column(name = "amount", precision = 18, scale = 2, nullable = false)
    private BigDecimal amount = BigDecimal.ZERO;

    // --- Bill Type (enum) ---
    @Column(name = "type")
    private Integer typeValue; // Lưu giá trị số (0, 1, ...)

    @Transient
    private BillType type;     // Enum dùng trong code

    // --- Bill Status (enum) ---
    @Column(name = "status", nullable = false)
    private Integer statusValue = 0; // 0 = pending

    @Transient
    private BillStatus status;

    @Column(name = "created_at", columnDefinition = "DATETIME2")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "paid_at", columnDefinition = "DATETIME2")
    private LocalDateTime paidAt;

    // ====== Mapping enum <-> value ======
    @PostLoad
    public void fillEnumFields() {
        if (this.statusValue != null) {
            this.status = BillStatus.fromInt(this.statusValue);
        }
        if (this.typeValue != null) {
            this.type = BillType.fromInt(this.typeValue);
        }
    }

    @PrePersist
    @PreUpdate
    public void fillValueFields() {
        if (this.status != null) {
            this.statusValue = this.status.getValue();
        }
        if (this.type != null) {
            this.typeValue = this.type.getValue();
        }
    }

    @Override
    public String toString() {
        return "Bill{" +
                "billId=" + billId +
                ", patient=" + (patient != null ? patient.getPatientId() : null) +
                ", appointment=" + (appointment != null ? appointment.getAppointmentId() : null) +
                ", labRequest=" + (labRequest != null ? labRequest.getLabRequestId() : null) +
                ", cashier=" + (cashier != null ? cashier.getUserId() : null) +
                ", amount=" + amount +
                ", type=" + type +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", paidAt=" + paidAt +
                '}';
    }
}
