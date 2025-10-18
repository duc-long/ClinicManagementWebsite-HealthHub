package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.dto.PatientUserDTO;
import com.group4.clinicmanagement.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {

    @Query("SELECT new com.group4.clinicmanagement.dto.PatientUserDTO(" +
            "u.userId, p.patientId, u.username, u.fullName, u.email, u.phone, u.genderValue, p.address, u.avatar, p.dateOfBirth) " +
            "FROM Patient p JOIN p.user u" +
            " WHERE u.username = :username")
    Optional<PatientUserDTO> fetchPatientWithUserInfoByUsername(@Param("username") String username);


    @Modifying
    @Transactional
    @Query("UPDATE Patient p SET p.address = :address, p.updatedAt = CURRENT_TIMESTAMP, p.dateOfBirth = :dateOfBirth " +
            "WHERE p.patientId = :patientId")
    int updateAddress(@Param("patientId") Integer patientId,
                      @Param("address") String address,
                      @Param("dateOfBirth") java.time.LocalDate dateOfBirth);
}
