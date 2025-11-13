package com.group4.clinicmanagement.entity;

import com.group4.clinicmanagement.enums.Gender;
import com.group4.clinicmanagement.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "Staff")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_id")
    private Integer staffId;

    @Column(name = "username", nullable = false, unique = true, length = 100, updatable = false)
    @NotBlank
    @Size(max = 100)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    @NotBlank
    private String passwordHash;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

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

    @Column(name = "avatar", length = 500)
    private String avatar;

    @Column(name = "gender", nullable = false)
    private Integer genderValue = 0;

    @Transient
    private Gender gender;

    @Column(name = "is_active", nullable = false)
    private Integer statusValue = 1;

    @Transient
    private UserStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "staff", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Doctor doctor;

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