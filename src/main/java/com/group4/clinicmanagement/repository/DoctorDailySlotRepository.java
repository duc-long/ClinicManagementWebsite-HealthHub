package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.entity.Doctor;
import com.group4.clinicmanagement.entity.DoctorDailySlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DoctorDailySlotRepository extends JpaRepository<DoctorDailySlot, Integer> {
    Optional<DoctorDailySlot> findByDoctorAndSlotDate(Doctor doctor, LocalDate sloDate );
}
