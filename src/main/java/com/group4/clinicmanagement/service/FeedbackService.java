package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.FeedbackDTO;
import com.group4.clinicmanagement.entity.Feedback;
import com.group4.clinicmanagement.repository.FeedbackRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    // Lấy tất cả feedback
    public List<FeedbackDTO> getAllFeedback() {
        return feedbackRepository.findAllFeedbacks();
    }

    // Lấy feedback theo patientId
    public List<Feedback> getFeedbackByPatientId(Integer patientId) {
        return feedbackRepository.findByPatient_PatientId(patientId);
    }

    public double getAverageRating() {
        List<Feedback> feedbacks = feedbackRepository.findAll();
        double totalRating = 0;
        int count = feedbacks.size();

        for (Feedback feedback : feedbacks) {
            totalRating += feedback.getRating();
        }

        return count > 0 ? totalRating / count : 0;
    }
}
