package com.group4.clinicmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "VitalSigns")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"medicalRecord", "doctor"})
public class VitalSigns {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vital_id")
    private Integer vitalId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", nullable = false, unique = true)
    private MedicalRecord medicalRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "height_cm")
    @Positive(message = "Height must be positive")
    @Max(value = 250, message = "Height cannot exceed 250 cm")
    private Double heightCm;

    @Column(name = "weight_kg")
    @Positive(message = "Weight must be positive")
    @Max(value = 300, message = "Weight cannot exceed 300 kg")
    private Double weightKg;

    @Column(name = "blood_pressure", length = 20)
    @Pattern(regexp = "^\\d{2,3}/\\d{2,3}$", message = "Blood pressure must be in format: 120/80")
    private String bloodPressure;

    @Column(name = "heart_rate")
    @Min(value = 30) @Max(value = 220)
    private Integer heartRate;

    @Column(name = "temperature")
    @DecimalMin(value = "30.0") @DecimalMax(value = "45.0")
    private Double temperature;

    @Column(name = "recorded_at", insertable = false, updatable = false)
    private LocalDateTime recordedAt;
}