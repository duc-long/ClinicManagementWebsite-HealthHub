package com.group4.clinicmanagement.entity;

import com.group4.clinicmanagement.enums.Gender;
import com.group4.clinicmanagement.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Patient")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Integer patientId;

    @Column(name = "username", nullable = false, unique = true, length = 100, updatable = false)
    @NotBlank
    @Size(max = 100)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    @NotBlank
    private String passwordHash;

    @Column(name = "full_name", nullable = false, length = 200)
    @NotBlank
    @Size(max = 200)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    @Column(name = "phone", nullable = false, unique = true, length = 20)
    @NotBlank
    @Pattern(regexp = "^\\+?[0-9]{10,15}$")
    @Size(max = 20)
    private String phone;

    @Column(name = "gender", nullable = false)
    private Integer genderValue = 0;

    @Transient
    private Gender gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "avatar", length = 500)
    private String avatar;

    @Column(name = "is_active", nullable = false)
    private Integer statusValue = 1;

    @Transient
    private UserStatus status;

    public UserStatus getStatus() {
        if (this.status == null && this.statusValue != null) {
            this.status = UserStatus.fromValue(this.statusValue);
        }
        return this.status != null ? this.status : UserStatus.INACTIVE;
    }

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @OneToMany(
            mappedBy = "patient",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<MedicalRecord> medicalRecords = new ArrayList<>();

    @OneToMany(
            mappedBy = "patient",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Feedback> feedbacks = new ArrayList<>();

    @OneToMany(
            mappedBy = "patient",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Appointment> appointments = new ArrayList<>();

    @OneToMany(
            mappedBy = "patient",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Bill> bills = new ArrayList<>();

    @OneToMany(
            mappedBy = "patient",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<PasswordResetToken> resetTokens = new ArrayList<>();

    @OneToMany(
            mappedBy = "patient",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<ConfirmAccount> confirmAccounts = new ArrayList<>();

    @PostLoad
    private void initEnums() {
        this.status = UserStatus.fromValue(this.statusValue);
        this.gender = Gender.fromValue(this.genderValue);
    }
    @PrePersist
    @PreUpdate
    private void persistEnumValues() {
        this.genderValue = (gender != null) ? gender.getValue() : 0;
        this.statusValue = (status != null) ? status.getValue() : 1;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
        this.genderValue = (gender != null) ? gender.getValue() : 0;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
        this.statusValue = (status != null) ? status.getValue() : 1;
    }
}