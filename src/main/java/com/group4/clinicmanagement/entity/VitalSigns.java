package com.group4.clinicmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Vitalsigns")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VitalSigns {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vital_id")
    private Integer vitalId;

    @OneToOne
    @JoinColumn(name = "record_id", nullable = false)
    private MedicalRecord medicalRecord; // FK -> MedicalRecord

    @ManyToOne
    @JoinColumn(name = "nurse_id", nullable = false)
    private User nurse; // FK -> Users (y tá)

    private Double heightCm;
    private Double weightKg;
    private String bloodPressure;
    private Integer heartRate;
    private Double temperature;
    private LocalDateTime recordedAt;
}