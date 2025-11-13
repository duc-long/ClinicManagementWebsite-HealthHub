package com.group4.clinicmanagement.entity;

import com.group4.clinicmanagement.enums.LabRequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "LabRequest")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lab_request_id")
    private Integer labRequestId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "record_id", nullable = false, unique = true)
    private MedicalRecord medicalRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private LabTestCatalog test;

    @Column(name = "status", nullable = false)
    private Integer statusValue;

    @Transient
    private LabRequestStatus status;

    @Column(name = "requested_at", insertable = false, updatable = false)
    private LocalDateTime requestedAt;

    @Column(name = "notes", length = 1000)
    private String notes;

    @OneToOne(
            mappedBy = "labRequest",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private LabResult labResult;

    @PostLoad
    private void loadEnum() {
        if (this.statusValue != null) {
            this.status = LabRequestStatus.fromInt(this.statusValue);
        }
    }

    @PrePersist
    @PreUpdate
    private void persistEnumValue() {
        if (this.status != null) {
            this.statusValue = this.status.getValue();
        }
    }

    public void setStatus(LabRequestStatus status) {
        this.status = status;
        this.statusValue = (status != null) ? status.getValue() : 0;
    }
}