package com.group4.clinicmanagement.entity;

import com.group4.clinicmanagement.enums.PrescriptionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Prescription")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prescription_id")
    private Integer prescriptionId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "record_id", nullable = false, unique = true)
    private MedicalRecord medicalRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "status", nullable = false)
    private Integer statusValue;

    @Transient
    private PrescriptionStatus status;

    @Column(name = "prescribed_at", insertable = false, updatable = false)
    private LocalDateTime prescribedAt;

    @OneToMany(
            mappedBy = "prescription",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},
            orphanRemoval = true)
    private List<PrescriptionDetail> details = new ArrayList<>();

    @PostLoad
    private void loadEnum() {
        if (this.statusValue != null) {
            this.status = PrescriptionStatus.fromInt(this.statusValue);
        }
    }

    @PrePersist
    @PreUpdate
    private void persistEnumValue() {
        if (this.status != null) {
            this.statusValue = this.status.getValue();
        }
    }

    public void setStatus(PrescriptionStatus status) {
        this.status = status;
        this.statusValue = (status != null) ? status.getValue() : 0;
    }
}