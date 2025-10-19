package com.group4.clinicmanagement.controller;

import com.group4.clinicmanagement.dto.FeedbackDTO;
import com.group4.clinicmanagement.service.FeedbackService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;


//    @GetMapping()
//    public String showFeedbackForm(Model model) {
//        model.addAttribute("feedbackDTO", new FeedbackDTO());
//        model.addAttribute("feedbacks", feedbackService.getRecentFeedbacks());
//        model.addAttribute("averageRating", feedbackService.getAverageRating());
//        return "feedback-form"; // Thymeleaf template
//    }


//    @PostMapping("/feedback")
//    public String submitFeedback(@ModelAttribute @Valid FeedbackDTO feedbackDTO,
//                                 BindingResult result,
//                                 RedirectAttributes redirectAttributes,
//                                 Model model) {
//        if (result.hasErrors()) {
//            model.addAttribute("feedbacks", feedbackService.getRecentFeedbacks());
//            model.addAttribute("averageRating", feedbackService.getAverageRating());
//            return "feedback-form";
//        }
//
//
//        feedbackService.submitFeedback(feedbackDTO);
//        redirectAttributes.addFlashAttribute("success", "Thank you for your feedback!");
//        return "redirect:/feedback";
//    }

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
