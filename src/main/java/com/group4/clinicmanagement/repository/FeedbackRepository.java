package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.dto.FeedbackDTO;
import com.group4.clinicmanagement.entity.Feedback;
import com.group4.clinicmanagement.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    @Query("SELECT AVG(f.rating) FROM Feedback f")
    Double getAverageRating();


    List<Feedback> findTop10ByOrderByCreatedAtDesc();

    Optional<Feedback> findByAppointment_AppointmentId(Integer appointmentId);
}
