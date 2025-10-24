package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.FeedbackDTO;
import com.group4.clinicmanagement.entity.Appointment;
import com.group4.clinicmanagement.entity.Feedback;
import com.group4.clinicmanagement.entity.Patient;
import com.group4.clinicmanagement.enums.AppointmentStatus;
import com.group4.clinicmanagement.repository.AppointmentRepository;
import com.group4.clinicmanagement.repository.FeedbackRepository;
import com.group4.clinicmanagement.repository.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;

    private final AppointmentRepository appointmentRepository;

    private final PatientRepository patientRepository;


    public FeedbackService(FeedbackRepository feedbackRepository, AppointmentRepository appointmentRepository, PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
        this.feedbackRepository = feedbackRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    public double getAverageRating() {
        return Optional.ofNullable(feedbackRepository.getAverageRating()).orElse(0.0);
    }

    @Transactional
    public Page<Feedback> getFeedbackPage(Pageable pageable) {
        return feedbackRepository.findAll(pageable);
    }

    @Transactional
    public List<Appointment> getEligibleAppointmentsForFeedback(Integer userId) {
        List<Appointment> allAppointments = appointmentRepository.findAllByPatient_PatientId(userId);

        List<Appointment> filtered = allAppointments.stream()
                .filter(app -> app.getStatus() == AppointmentStatus.EXAMINED)
                .filter(app -> feedbackRepository.findByAppointment_AppointmentId(app.getAppointmentId()).isEmpty())
                .toList();
        return filtered;
    }

    @Transactional
    public boolean submitFeedback(Integer userId, FeedbackDTO feedbackDTO) {
        System.out.println("==== SUBMIT FEEDBACK DEBUG START ====");
        System.out.println("userId = " + userId);
        System.out.println("appointmentId = " + feedbackDTO.getAppointmentId());
        System.out.println("rating = " + feedbackDTO.getRating());
        System.out.println("comment = " + feedbackDTO.getComment());
        // 1️⃣ Kiểm tra Appointment có tồn tại không
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(feedbackDTO.getAppointmentId());
        if (appointmentOpt.isEmpty()) {
            System.out.println("❌ Appointment not found");
            return false;
        }

        Appointment appointment = appointmentOpt.get();
        System.out.println("✅ Found appointment with status = " + appointment.getStatusValue());
        System.out.println("✅ Appointment.patientId = " + (appointment.getPatient() != null ? appointment.getPatient().getPatientId() : null));

        // 2️⃣ Check xem appointment này thuộc về user hiện tại không
        if (appointment.getPatient() == null || !appointment.getPatient().getPatientId().equals(userId)) {
            System.out.println("a");
            return false;
        }

        // 3️⃣ Check xem appointment đã khám chưa
        if (appointment.getStatusValue() != AppointmentStatus.EXAMINED.getValue()) {
            System.out.println("b");
            return false;
        }

        // 4️⃣ Check xem appointment này đã có feedback chưa
        if (feedbackRepository.findByAppointment_AppointmentId(appointment.getAppointmentId()).isPresent()) {
            System.out.println("c");
            return false;
        }

        // 5️⃣ Lấy thông tin bệnh nhân
        Patient patient = patientRepository.findById(userId).orElse(null);
        if (patient == null) {
            System.out.println("❌ Patient not found!");
            return false;
        }

        // 6️⃣ Lưu Feedback
        Feedback feedback = new Feedback();
        feedback.setAppointment(appointment);
        feedback.setPatient(patient);
        feedback.setRating(feedbackDTO.getRating());
        feedback.setComment(feedbackDTO.getComment());
        feedback.setCreatedAt(LocalDateTime.now());

        feedbackRepository.save(feedback);
        return true;
    }

    @Transactional
    public Page<Feedback> getFeedbackPageForUser(Integer userId, Pageable pageable) {
        List<Feedback> allFeedbacks = new ArrayList<>();

        // Feedback của chính user (nếu có)
        List<Feedback> userFeedbacks = feedbackRepository.findAllByPatient_PatientIdOrderByCreatedAtDesc(userId);

        if (!userFeedbacks.isEmpty()) {
            // Nếu user có feedback, lấy feedback của họ lên đầu
            allFeedbacks.addAll(userFeedbacks);

            // Sau đó lấy thêm feedback của người khác
            Page<Feedback> others = feedbackRepository.findAll(pageable);
            others.stream()
                    .filter(f -> !f.getPatient().getUser().getUserId().equals(userId))
                    .forEach(allFeedbacks::add);
        } else {
            // Nếu chưa có feedback nào, chỉ hiển thị feedback mới nhất
            Page<Feedback> newest = feedbackRepository.findAll(pageable);
            allFeedbacks.addAll(newest.getContent());
        }

        return new PageImpl<>(allFeedbacks, pageable, allFeedbacks.size());
    }

    public List<Feedback> getLatestFeedbackByUser(Integer userId) {
        return feedbackRepository.findAllByPatient_PatientIdOrderByCreatedAtDesc(userId);
    }

    public Page<Feedback> getFeedbackPageExcludeUser(Integer userId, Pageable pageable) {
        return feedbackRepository.findByPatient_PatientIdNot(userId, pageable);
    }

    public boolean updateFeedback(FeedbackDTO form, Integer userId) {
        Feedback existing = feedbackRepository.findById(form.getFeedbackId()).orElse(null);
        if (existing == null || !existing.getPatient().getPatientId().equals(userId)) {
            return false; // không cho update người khác
        }

        existing.setComment(form.getComment());
        existing.setRating(form.getRating());
        existing.setCreatedAt(LocalDateTime.now()); // cập nhật thời gian sửa
        feedbackRepository.save(existing);
        return true;
    }

    @Transactional
    public boolean deleteFeedback(Integer userId, Integer feedbackId) {
        Optional<Feedback> feedbackOpt = feedbackRepository.findById(feedbackId);
        if (feedbackOpt.isEmpty()) {
            return false; // không tồn tại
        }

        Feedback feedback = feedbackOpt.get();

        // ✅ Chỉ cho phép xóa nếu là feedback của chính user
        if (feedback.getPatient() == null || !feedback.getPatient().getPatientId().equals(userId)) {
            return false; // không phải chủ sở hữu
        }

        feedbackRepository.delete(feedback);
        return true;
    }
}
