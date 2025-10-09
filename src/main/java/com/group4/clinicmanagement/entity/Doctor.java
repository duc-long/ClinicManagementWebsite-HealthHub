package com.group4.clinicmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Doctor")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    @Id
    @Column(name = "doctor_id")
    private Integer doctorId; // = Users.user_id

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "doctor_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "license_no", nullable = false, unique = true, length = 50)
    private String licenseNo;

    @Column(nullable = false, length = 100)
    private String specialty;

    @Column(length = 100)
    private String degree;
    private Integer yearsExperience;

    @Column(length = 1000)
    private String bio;
    private Boolean profileVisibility = true;
    private LocalDateTime createdAt;
}
