package com.group4.clinicmanagement.entity;

import com.group4.clinicmanagement.enums.Gender;
import com.group4.clinicmanagement.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(nullable = false, unique = true, length = 100, updatable = false)
    private String username;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(nullable = false, length = 200)
    private String fullName;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 20)
    private String phone;

    // Relations
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Patient patient;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Doctor doctor;

    @Column(name = "gender")
    private Integer genderValue;

    @Transient
    private Gender gender;


    @Column(name = "is_active")
    private Integer statusValue;

    @Transient
    private UserStatus status;

    private String avatar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PostLoad
    public void loadEnums() {
        this.gender = Gender.fromInt(this.genderValue != null ? this.genderValue : 0);
        this.status = UserStatus.fromInt(this.statusValue != null ? this.statusValue : 0);
    }

    @PrePersist
    @PreUpdate
    public void persistEnumValues() {
        this.genderValue = (gender != null) ? gender.getValue() : 0;
        this.statusValue = (status != null) ? status.getValue() : 0;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
        this.genderValue = (gender != null) ? gender.getValue() : 0;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
        this.statusValue = (status != null) ? status.getValue() : 0;
    }
}
