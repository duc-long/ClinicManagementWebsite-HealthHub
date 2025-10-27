package com.group4.clinicmanagement.entity;

import com.group4.clinicmanagement.enums.RecordStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "medicalrecord")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer recordId;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @OneToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(length = 2000)
    private String diagnosis;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String notes;

    @Column(name = "status")
    private Integer statusValue;

    @Transient
    private RecordStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "medicalRecord")
    private List<VitalSigns> vitalSigns;

    @OneToMany(mappedBy = "medicalRecord")
    private List<LabRequest> labRequests;

    @OneToMany(mappedBy = "medicalRecord")
    private List<Prescription> prescriptions;

    @PostLoad
    public void loadEnum() {
        this.status = RecordStatus.fromInt(this.statusValue != null ? this.statusValue : 0);
    }

    @PrePersist
    @PreUpdate
    public void persistEnumValue() {
        this.statusValue = (status != null) ? status.getValue() : 0;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
        this.statusValue = (status != null) ? status.getValue() : 0;
    }
}
