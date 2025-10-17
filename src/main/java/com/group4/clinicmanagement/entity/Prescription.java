package com.group4.clinicmanagement.entity;

import com.group4.clinicmanagement.enums.PrescriptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Prescription")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer prescriptionId;

    @ManyToOne
    @JoinColumn(name = "record_id", nullable = false)
    private MedicalRecord medicalRecord;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "status")
    private Integer statusValue;

    @Transient
    private PrescriptionStatus status;

    @Column(name = "prescribed_at")
    private LocalDateTime prescribedAt;

    @OneToMany(mappedBy = "prescription", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PrescriptionDetail> details;

    @PostLoad
    public void loadEnum() {
        this.status = PrescriptionStatus.fromInt(this.statusValue != null ? this.statusValue : 0);
    }

    @PrePersist
    @PreUpdate
    public void persistEnumValue() {
        this.statusValue = (status != null) ? status.getValue() : 0;
    }

    public void setStatus(PrescriptionStatus status) {
        this.status = status;
        this.statusValue = (status != null) ? status.getValue() : 0;
    }
}
