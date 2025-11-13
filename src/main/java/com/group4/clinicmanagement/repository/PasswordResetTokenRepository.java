package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    // SỬA: Tìm theo patient.email
    Optional<PasswordResetToken> findByPatient_Email(String email);

    // SỬA: Xóa theo patient.email
    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.patient.email = :email")
    void deleteByPatientEmail(@Param("email") String email);
}
