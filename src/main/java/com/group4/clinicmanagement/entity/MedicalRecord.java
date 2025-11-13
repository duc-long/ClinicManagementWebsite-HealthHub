package com.group4.clinicmanagement.entity;

import com.group4.clinicmanagement.enums.RecordStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medicalrecord")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Integer recordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private Staff createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "diagnosis", length = 2000)
    private String diagnosis;

    @Column(name = "notes", columnDefinition = "NVARCHAR(MAX)")
    private String notes;

    @Column(name = "status", nullable = false)
    private Integer statusValue;

    @Transient
    private RecordStatus status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToOne(
            mappedBy = "medicalRecord",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},
            orphanRemoval = true)
    private VitalSigns vitalSigns;

    @OneToOne(
            mappedBy = "medicalRecord",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private LabRequest labRequest;

    @OneToOne(
            mappedBy = "medicalRecord",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},
            orphanRemoval = true)
    private Prescription prescription;

    @PostLoad
    private void loadEnum() {
        if (this.statusValue != null) {
            this.status = RecordStatus.fromInt(this.statusValue);
        }
    }

    @PrePersist
    @PreUpdate
    private void persistEnumValue() {
        if (this.status != null) {
            this.statusValue = this.status.getValue();
        }
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
        this.statusValue = (status != null) ? status.getValue() : 0;
    }
}