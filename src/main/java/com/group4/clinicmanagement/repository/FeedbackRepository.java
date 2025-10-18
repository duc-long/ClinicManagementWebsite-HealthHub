package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.dto.FeedbackDTO;
import com.group4.clinicmanagement.entity.Feedback;
import com.group4.clinicmanagement.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    List<Feedback> findAll();

    List<Feedback> findByPatient_PatientId(Integer patientPatientId);

    @Query("SELECT new com.group4.clinicmanagement.dto.FeedbackDTO(f.feedbackId, f.rating, f.comment, f.createdAt, " +
            "mr.diagnosis, mr.createdAt, p.user.fullName, u.username, u.avatar) " +
            "FROM Feedback f " +
            "JOIN f.patient p " +
            "JOIN p.user u " + // Nếu bạn vẫn cần dùng Appointment, để lấy thông tin Appointment, nếu không thì có thể bỏ đi
            "JOIN p.medicalRecords mr") // Join trực tiếp từ Patient vào MedicalRecord
    List<FeedbackDTO> findAllFeedbacks();
}
