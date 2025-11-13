package com.group4.clinicmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "doctorcertificate")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorCertificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certificate_id")
    private Integer certificateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "certificate_name", nullable = false, length = 200)
    @NotBlank(message = "Certificate name is required")
    @Size(max = 200)
    private String certificateName;

    @Column(name = "issued_by", nullable = false, length = 200)
    @NotBlank(message = "Issuing authority is required")
    @Size(max = 200)
    private String issuedBy;

    @Column(name = "certificate_number", length = 100)
    @Size(max = 100)
    private String certificateNumber;

    @Column(name = "issued_date")
    private LocalDate issuedDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "file_path", length = 1000)
    @Size(max = 1000)
    private String filePath;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Transient
    public boolean isActive() {
        return expiryDate == null || expiryDate.isAfter(LocalDate.now());
    }

    @Transient
    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }

}