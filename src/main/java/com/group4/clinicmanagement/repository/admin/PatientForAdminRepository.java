package com.group4.clinicmanagement.repository.admin;

import com.group4.clinicmanagement.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientForAdminRepository extends JpaRepository<Patient,Integer> {
    Page<Patient> findAll(Pageable pageable);

    Optional<Patient> getPatientsByPatientId(Integer patientId);
}
