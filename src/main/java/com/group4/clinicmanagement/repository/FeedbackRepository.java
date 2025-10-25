package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.dto.FeedbackDTO;
import com.group4.clinicmanagement.entity.Feedback;
import com.group4.clinicmanagement.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    @Query("SELECT AVG(f.rating) FROM Feedback f")
    Double getAverageRating();

    Optional<Feedback> findByAppointment_AppointmentId(Integer appointmentId);

    Page<Feedback> findAll(Pageable pageable);

    @Query("SELECT f FROM Feedback f WHERE f.patient.patientId <> :userId")
    Page<Feedback> findByPatient_PatientIdNot(@Param("userId") Integer userId, Pageable pageable);

    List<Feedback> findAllByPatient_PatientIdOrderByCreatedAtDesc(Integer userId);
}
