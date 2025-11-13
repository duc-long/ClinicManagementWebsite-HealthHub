package com.group4.clinicmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "passwordresettoken")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "token", nullable = false, unique = true, length = 255)
    @NotBlank
    private String token;

    @Column(name = "otp_code", length = 10, unique = true)
    private String otpCode;

    @Column(name = "expiration_date", nullable = false)
    private LocalDateTime expirationDate;

    @Column(name = "attempts", nullable = false)
    private int attempts = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Transient
    public boolean isExpired() {
        return expirationDate != null && expirationDate.isBefore(LocalDateTime.now());
    }

    @Transient
    public boolean isOtpValid(String inputOtp) {
        return otpCode != null && otpCode.equals(inputOtp) && !isExpired();
    }

    public void incrementAttempts() {
        this.attempts++;
    }

    public void resetAttempts() {
        this.attempts = 0;
    }
}