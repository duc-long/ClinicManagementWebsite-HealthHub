package com.group4.clinicmanagement.repository.admin;

import com.group4.clinicmanagement.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorForAdminRepository extends JpaRepository<Doctor, Integer> {
    Page<Doctor> findAll(Pageable pageable);

    Optional<Doctor> getDoctorsByDoctorId(Integer doctorId);

    Optional<Doctor> findDoctorsByLicenseNo(String licenseNo);

}
