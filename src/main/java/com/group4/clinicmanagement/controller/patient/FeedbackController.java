package com.group4.clinicmanagement.controller.patient;

import com.group4.clinicmanagement.dto.FeedbackDTO;
import com.group4.clinicmanagement.security.CustomUserDetails;
import com.group4.clinicmanagement.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public String submitFeedback(@Valid @ModelAttribute FeedbackDTO feedbackDTO,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {

        // ✅ Lấy user từ Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            redirectAttributes.addFlashAttribute("error", "You must log in to submit feedback.");
            return "redirect:/patient/login";
        }

        Integer userId = userDetails.getPatient().getPatientId();

        // Kiểm tra validation cơ bản
        if (result.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder("Please fix the following errors:");
            result.getFieldErrors().forEach(err ->
                    errorMessage.append("<br>")
                            .append(err.getDefaultMessage())
            );
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/home";
        }

        // Kiểm tra logic nghiệp vụ
        boolean success = feedbackService.submitFeedback(userId, feedbackDTO);

        if (!success) {
            redirectAttributes.addFlashAttribute("error", "You are not eligible to give feedback for this appointment.");
        } else {
            redirectAttributes.addFlashAttribute("success", "Thank you! Your feedback has been submitted successfully.");
        }

        return "redirect:/home";
    }

    // 🟡 Xử lý khi submit form edit
    @PostMapping("/update-feedback")
    public String updateFeedback(@Valid @ModelAttribute FeedbackDTO feedbackForm, BindingResult result, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return "redirect:/patient/login";
        }
        // Kiểm tra validation cơ bản
        if (result.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder("Please fix the following errors:");
            result.getFieldErrors().forEach(err ->
                    errorMessage.append("<br>")
                            .append(err.getDefaultMessage())
            );
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/home";
        }

        boolean updated = feedbackService.updateFeedback(feedbackForm, userDetails.getUserId());
        if (updated) {
            redirectAttributes.addFlashAttribute("success", "Feedback updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Unable to update feedback.");
        }

        return "redirect:/home";
    }

    @PostMapping("/delete/{id}")
    public String deleteFeedback(@PathVariable("id") Integer feedbackId,
                                 RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // ✅ Kiểm tra đăng nhập
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            redirectAttributes.addFlashAttribute("error", "You must log in to delete feedback.");
            return "redirect:/patient/login";
        }

        Integer userId = userDetails.getPatient().getPatientId();

        boolean deleted = feedbackService.deleteFeedback(userId, feedbackId);

        if (deleted) {
            redirectAttributes.addFlashAttribute("success", "Feedback has been deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "You can only delete your own feedback or feedback not found.");
        }

        return "redirect:/home";
    }
}
