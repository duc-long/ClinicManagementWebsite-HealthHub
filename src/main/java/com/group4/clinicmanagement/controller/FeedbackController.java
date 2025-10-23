package com.group4.clinicmanagement.controller;

import com.group4.clinicmanagement.dto.FeedbackDTO;
import com.group4.clinicmanagement.entity.Feedback;
import com.group4.clinicmanagement.service.FeedbackService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @PostMapping()
    public String submitFeedback(@ModelAttribute FeedbackDTO feedbackDTO,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        Integer userId = (Integer) session.getAttribute("userId");

        if (!feedbackService.canGiveFeedback(userId, feedbackDTO.getAppointmentId())) {
            redirectAttributes.addFlashAttribute("error", "Bạn không đủ điều kiện để gửi phản hồi.");
            return "redirect:/home";
        }

        feedbackService.submitFeedback(feedbackDTO);
        redirectAttributes.addFlashAttribute("success", "Gửi phản hồi thành công.");
        return "redirect:/home";
    }
}
