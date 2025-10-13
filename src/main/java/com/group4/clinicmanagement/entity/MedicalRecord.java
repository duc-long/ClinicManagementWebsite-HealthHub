package com.group4.clinicmanagement.entity;

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

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @OneToMany(mappedBy = "medicalRecord")
    private List<VitalSigns> vitalSigns;

    @OneToMany(mappedBy = "medicalRecord")
    private List<LabRequest> labRequests;

    @OneToMany(mappedBy = "medicalRecord")
    private List<Prescription> prescriptions;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    private String diagnosis;
    private String notes;
    private Integer status; // 0=open,1=closed
    private LocalDateTime
            createdAt;

}
