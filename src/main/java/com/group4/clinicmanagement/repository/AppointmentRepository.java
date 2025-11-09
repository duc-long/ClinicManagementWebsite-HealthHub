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
import java.util.Optional;

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
          AND a.appointmentDate = CURRENT_DATE
          AND a.statusValue = 5
        ORDER BY a.appointmentDate DESC
    """)
    List<Appointment> findTodayAppointments(@Param("doctorId") Integer doctorId,
                                            @Param("patientName") String patientName);

    @Query(
            value = """
        SELECT a FROM Appointment a
        LEFT JOIN a.patient p
        LEFT JOIN p.user pu
        LEFT JOIN a.doctor d
        LEFT JOIN d.user du
        LEFT JOIN a.receptionist r
        WHERE a.statusValue = :status
        ORDER BY a.appointmentDate DESC
    """,
            countQuery = "SELECT COUNT(a) FROM Appointment a WHERE a.statusValue = :status"
    )
    Page<Appointment> findStatusValueDesc(@Param("status") int status, Pageable pageable);

    @Query("""
    SELECT a FROM Appointment a
    LEFT JOIN FETCH a.patient p
    LEFT JOIN FETCH p.user pu
    LEFT JOIN FETCH a.doctor d
    LEFT JOIN FETCH d.user du
    LEFT JOIN FETCH a.receptionist r
    WHERE a.appointmentId = :id
      AND a.statusValue IN(0,1,2,4,5)
""")
    Optional<Appointment> findIdWithStatusRangeforRecep(@Param("id") int id);

    @Query("""
    SELECT COALESCE(MAX(a.queueNumber), 0)
    FROM Appointment a
    WHERE a.doctor.doctorId = :doctorId
      AND a.appointmentDate = :date
""")
    Integer findMaxQueueNumber(@Param("doctorId") int doctorId,
                                              @Param("date") LocalDate date);


    @Query("""
    SELECT a FROM Appointment a
    LEFT JOIN FETCH a.patient p
    LEFT JOIN FETCH p.user pu
    LEFT JOIN FETCH a.doctor d
    LEFT JOIN FETCH d.user du
    LEFT JOIN FETCH a.receptionist r
    WHERE a.appointmentId = :id
      AND a.statusValue IN(3,5)
""")
    Optional<Appointment> findIdWithStatusRangeforCash(@Param("id") int id);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.doctorId = :doctorId AND a.statusValue = 5 " +
            "AND a.appointmentDate = CURRENT_DATE ORDER BY a.appointmentDate DESC")
    List<Appointment> findTodayAppointmentsByDoctorId(@Param("doctorId") Integer doctorId);
}