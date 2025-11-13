package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.dto.PatientUserDTO;
import com.group4.clinicmanagement.entity.Patient;
import com.group4.clinicmanagement.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {

    @Query("SELECT new com.group4.clinicmanagement.dto.PatientUserDTO(" +
            "p.patientId, p.patientId, p.username, p.fullName, p.email, p.phone, p.genderValue, p.address, p.avatar, p.dateOfBirth) " +
            "FROM Patient p" +
            " WHERE p.username = :username")
    Optional<PatientUserDTO> fetchPatientWithUserInfoByUsername(@Param("username") String username);


    @Modifying
    @Transactional
    @Query("UPDATE Patient p SET p.address = :address, p.updatedAt = CURRENT_TIMESTAMP, p.dateOfBirth = :dateOfBirth " +
            "WHERE p.patientId = :patientId")
    int updateAddress(@Param("patientId") Integer patientId,
                      @Param("address") String address,
                      @Param("dateOfBirth") java.time.LocalDate dateOfBirth);

    @Modifying
    @Query("UPDATE Staff u SET u.passwordHash = :newHash WHERE u.username = :username")
    void updatePasswordHashByUsername(@Param("username") String username, @Param("newHash") String newHash);

    @Query("SELECT COUNT(p) FROM Patient p WHERE CAST(p.createdAt AS date) = CAST(:today AS date)")
    long countPatientsToday(@Param("today") LocalDateTime today);

    @Query("SELECT COUNT(p) FROM Patient p WHERE YEAR(p.createdAt) = :year AND MONTH(p.createdAt) = :month")
    long countPatientsThisMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT COUNT(p) FROM Patient p WHERE YEAR(p.createdAt) = :year")
    long countPatientsThisYear(@Param("year") int year);

    Optional<Patient> findPatientByUsername(@Param("username") String username);

    @Modifying
    @Transactional
    @Query("UPDATE Patient u SET u.fullName = :fullName, u.email = :email, " +
            "u.phone = :phone, u.genderValue = :gender, u.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE u.username = :username")
    int updateProfileByUsername(@Param("username") String username,
                                @Param("fullName") String fullName,
                                @Param("email") String email,
                                @Param("phone") String phone,
                                @Param("gender") Integer gender);

    @Modifying
    @Query("UPDATE Patient u SET u.avatar = :filename WHERE u.username = :username")
    void updateAvatarFilename(@Param("username") String username,
                              @Param("filename") String filename);

    Optional<Patient> findByUsername(String username);

}

