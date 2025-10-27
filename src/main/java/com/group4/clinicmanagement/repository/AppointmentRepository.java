package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.entity.Appointment;
import com.group4.clinicmanagement.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // method to count total appointment in day
    @Query("""
        SELECT COUNT(a) FROM Appointment a
        WHERE a.doctor.doctorId = :doctorId
          AND a.appointmentDate = CURRENT_DATE
    """)
    int countTodayAppointments(@Param("doctorId") int doctorId);

    List<Appointment> findByStatusValueOrderByDoctor_DoctorIdAsc(Integer status);

    // method to find all appointments for doctor with "CHECKED-IN" status
    List<Appointment> findByDoctor_DoctorIdAndStatusValue(Integer doctorId, Integer statusValue);

    // method to count waiting appointment
    @Query("""
        SELECT COUNT(a) FROM Appointment a
        WHERE a.doctor.doctorId = :doctorId
          AND a.appointmentDate = CURRENT_DATE
          AND a.statusValue = 6
    """)
    int countExaminedTodayAppointments(@Param("doctorId") Integer doctorId);

    // method to get all appointment today with pagination
    @Query(value = """
        SELECT a FROM Appointment a
        JOIN a.patient p
        JOIN p.user u
        WHERE a.doctor.doctorId = :doctorId
          AND (:patientName IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :patientName, '%')))
          AND (:status IS NULL OR a.statusValue = :status)
          AND a.appointmentDate = CURRENT_DATE
        ORDER BY a.appointmentDate DESC
    """,
            countQuery = """
        SELECT COUNT(a) FROM Appointment a
        JOIN a.patient p
        JOIN p.user u
        WHERE a.doctor.doctorId = :doctorId
          AND (:patientName IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :patientName, '%')))
          AND (:status IS NULL OR a.statusValue = :status)
          AND a.appointmentDate = CURRENT_DATE
    """)
    Page<Appointment> findTodayAppointmentsPaged(@Param("doctorId") Integer doctorId,
                                                 @Param("patientName") String patientName,
                                                 @Param("status") AppointmentStatus status,
                                                 Pageable pageable);
}