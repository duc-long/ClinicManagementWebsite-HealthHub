package com.group4.clinicmanagement.entity;

import com.group4.clinicmanagement.enums.LabRequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Labrequest")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer labRequestId;

    @ManyToOne
    @JoinColumn(name = "record_id", nullable = false)
    private MedicalRecord medicalRecord;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "test_id", nullable = false)
    private LabTestCatalog test;

    @Column(name = "status")
    private Integer statusValue;

    @Transient
    private LabRequestStatus status;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @OneToMany(mappedBy = "labRequest")
    private List<LabResult> labResults;

    @PostLoad
    public void loadEnum() {
        this.status = LabRequestStatus.fromInt(this.statusValue != null ? this.statusValue : 0);
    }

    @PrePersist
    @PreUpdate
    public void persistEnumValue() {
        this.statusValue = (status != null) ? status.getValue() : 0;
    }

    public void setStatus(LabRequestStatus status) {
        this.status = status;
        this.statusValue = (status != null) ? status.getValue() : 0;
    }
}
