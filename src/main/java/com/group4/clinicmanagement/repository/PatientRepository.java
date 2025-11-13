package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.dto.PatientUserDTO;
import com.group4.clinicmanagement.entity.Patient;
import com.group4.clinicmanagement.entity.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @Query("UPDATE Patient p SET " +
            "p.fullName = :fullName, " +
            "p.email = :email, " +
            "p.phone = :phone, " +
            "p.genderValue = :genderValue, " +
            "p.address = :address, " +
            "p.dateOfBirth = :dateOfBirth, " +
            "p.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE p.username = :username")
    int updatePatientProfile(
            @Param("username") String username,
            @Param("fullName") String fullName,
            @Param("email") String email,
            @Param("phone") String phone,
            @Param("genderValue") Integer genderValue,
            @Param("address") String address,
            @Param("dateOfBirth") java.time.LocalDate dateOfBirth
    );

    @Modifying
    @Transactional
    @Query("UPDATE Patient p SET p.avatar = :avatarFilename, p.updatedAt = CURRENT_TIMESTAMP WHERE p.username = :username")
    int updateAvatarFilename(@Param("username") String username, @Param("avatarFilename") String avatarFilename);

    @Modifying
    @Transactional
    @Query("UPDATE Patient p SET p.address = :address, p.updatedAt = CURRENT_TIMESTAMP, p.dateOfBirth = :dateOfBirth " +
            "WHERE p.patientId = :patientId")
    int updateAddress(@Param("patientId") Integer patientId,
                      @Param("address") String address,
                      @Param("dateOfBirth") java.time.LocalDate dateOfBirth);

    @Modifying
    @Transactional
    @Query("UPDATE Patient u SET u.passwordHash = :newHash WHERE u.username = :username")
    void updatePasswordHashByUsername(@Param("username") String username, @Param("newHash") String newHash);

    @Query("SELECT COUNT(p) FROM Patient p WHERE CAST(p.createdAt AS date) = CAST(:today AS date)")
    long countPatientsToday(@Param("today") LocalDateTime today);

    @Query("SELECT COUNT(p) FROM Patient p WHERE YEAR(p.createdAt) = :year AND MONTH(p.createdAt) = :month")
    long countPatientsThisMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT COUNT(p) FROM Patient p WHERE YEAR(p.createdAt) = :year")
    long countPatientsThisYear(@Param("year") int year);

    Optional<Patient> findPatientByUsername(@Param("username") String username);

    Optional<Patient> findByUsername(String username);

    Page<Patient> findAll(Pageable pageable);

    Optional<Patient> getPatientsByPatientId(Integer patientId);

    Optional<Patient> findPatientByPatientId(Integer patientId);

    Optional<Patient> findPatientByemail(String email);

    Optional<Patient> findPatientByPhone(String phone);

    Optional<Patient> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}

