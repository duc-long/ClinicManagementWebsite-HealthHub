package com.group4.clinicmanagement.entity;

import com.group4.clinicmanagement.enums.Gender;
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

    @Column(nullable = false, unique = true, length = 100)
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

    // Liên kết 1-1 với từng loại role chi tiết
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Patient patient;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Doctor doctor;


    @Column(name = "gender")
    private Integer genderValue;


    @Transient
    private Gender gender;

    private Boolean isActive = true;
    private String avatar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PostLoad
    public void loadGenderEnum() {
        this.gender = Gender.fromInt(this.genderValue != null ? this.genderValue : 0);
    }

    @PrePersist
    @PreUpdate
    public void persistGenderValue() {
        if (this.gender != null) {
            this.genderValue = this.gender.getValue();
        }
    }
    public void setGender(Gender gender) {
        this.gender = gender;
        this.genderValue = (gender != null) ? gender.getValue() : 0;
    }
}
