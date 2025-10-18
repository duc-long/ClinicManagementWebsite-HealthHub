package com.group4.clinicmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Patient")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Patient {
    @Id
    private Integer patientId; // = User.user_id

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "patient_id")
    private User user;

    @OneToMany(mappedBy = "patient")
    private List<MedicalRecord> medicalRecords;

    @OneToMany(mappedBy = "patient")
    private List<Feedback> feedbacks;

    private LocalDate dateOfBirth;

    @Column(length = 255)
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
