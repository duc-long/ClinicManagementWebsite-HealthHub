package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.FeedbackDTO;
import com.group4.clinicmanagement.entity.Appointment;
import com.group4.clinicmanagement.entity.Feedback;
import com.group4.clinicmanagement.entity.Patient;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.enums.AppointmentStatus;
import com.group4.clinicmanagement.repository.AppointmentRepository;
import com.group4.clinicmanagement.repository.FeedbackRepository;
import com.group4.clinicmanagement.repository.PatientRepository;
import com.group4.clinicmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;

    private final PatientRepository patientRepository;

    private final AppointmentRepository appointmentRepository;

    private final UserRepository userRepository;

    public FeedbackService(FeedbackRepository feedbackRepository, AppointmentRepository appointmentRepository, PatientRepository patientRepository, UserRepository userRepository) {
        this.patientRepository = patientRepository;
        this.feedbackRepository = feedbackRepository;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }


    public void submitFeedback(FeedbackDTO dto) {
        Feedback feedback = new Feedback();
        feedback.setRating(dto.getRating());
        feedback.setComment(dto.getComment());


        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        feedback.setPatient(patient);


        feedbackRepository.save(feedback);
    }


    public List<Feedback> getRecentFeedbacks() {
        return feedbackRepository.findTop10ByOrderByCreatedAtDesc();
    }


    public double getAverageRating() {
        return Optional.ofNullable(feedbackRepository.getAverageRating()).orElse(0.0);
    }

    public boolean canGiveFeedback(Integer userId, Integer appointmentId) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isEmpty()) return false;

        Appointment appointment = appointmentOpt.get();
        if (!appointment.getPatient().getPatientId().equals(userId)) return false;
        if (!appointment.getStatus().equals(AppointmentStatus.EXAMINED)) return false;

        return feedbackRepository.findByAppointment_AppointmentId(appointmentId).isEmpty();
    }

    public List<Appointment> getEligibleAppointmentsForFeedback(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Patient patient = (Patient) patientRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        List<Appointment> allAppointments = appointmentRepository.findAllByPatient_PatientId(patient.getPatientId());
        return allAppointments.stream()
                .filter(app -> app.getStatus().equals(AppointmentStatus.EXAMINED)) // đã khám
                .filter(app -> feedbackRepository.findByAppointment_AppointmentId(app.getAppointmentId()).isEmpty()) // chưa feedback
                .toList();
    }

    public Page<Feedback> getFeedbackPage(Pageable pageable) {
        return feedbackRepository.findAll(pageable);
    }
}
