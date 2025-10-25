package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment,Integer> {
    List<Appointment> findAllByPatient_PatientId(Integer patientId);

    // get the number of appointment in a day
    @Query("SELECT COUNT(a) FROM Appointment a " +
            "WHERE a.patient.patientId = :patientId " +
            "AND CAST(a.createdAt AS date) = :targetDate")
    int countAppointmentsInDay(@Param("patientId") int patientId,
                               @Param("targetDate") LocalDate targetDate);

    List<Appointment> findByStatusValueOrderByDoctor_DoctorIdAsc(Integer status);

    // method to find all appointments for doctor with "CHECKED-IN" status
    List<Appointment> findByDoctor_DoctorIdAndStatusValue(Integer doctorId, Integer statusValue);
}