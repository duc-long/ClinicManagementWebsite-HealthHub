package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment,Integer> {
    List<Appointment> findAllByPatient_PatientId(Integer patientId);

    // get the number of appointment in a day
    int countByAppointmentDate(@Param("date") LocalDate date);
}
