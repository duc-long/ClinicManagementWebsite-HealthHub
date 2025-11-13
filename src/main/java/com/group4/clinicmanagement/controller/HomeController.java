package com.group4.clinicmanagement.controller;

import com.group4.clinicmanagement.dto.DepartmentDTO;
import com.group4.clinicmanagement.dto.DoctorHomeDTO;
import com.group4.clinicmanagement.entity.Appointment;
import com.group4.clinicmanagement.entity.Feedback;
import com.group4.clinicmanagement.security.CustomUserDetails;
import com.group4.clinicmanagement.service.DepartmentService;
import com.group4.clinicmanagement.service.DoctorService;
import com.group4.clinicmanagement.service.FeedbackService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping(value = "/home")
public class HomeController {

    private final DoctorService doctorService;
    private final FeedbackService feedbackService;
    private final DepartmentService departmentService;

    public HomeController(DoctorService doctorService, FeedbackService feedbackService, DepartmentService departmentService) {
        this.feedbackService = feedbackService;
        this.doctorService = doctorService;
        this.departmentService = departmentService;
    }

    @GetMapping()
    public String guestHome(Model model,@RequestParam(defaultValue = "1") int page, HttpSession session) {
        int pageSize = 3;
        if (page < 1) {
            return "redirect:/home?page=1";
        }
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());

        List<DoctorHomeDTO> doctorUserDTOS = doctorService.findTopDoctors(PageRequest.of(0, 4));
        model.addAttribute("doctors", doctorUserDTOS);
        List<DepartmentDTO> departments = departmentService.findAll();
        model.addAttribute("departments", departments);
        double averageRating = feedbackService.getAverageRating();
        model.addAttribute("averageRating", averageRating);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            userId = userDetails.getUserId();
            List<Appointment> eligibleAppointments = feedbackService.getEligibleAppointmentsForFeedback(userId);
            model.addAttribute("eligibleAppointments", eligibleAppointments);
        }

        Page<Feedback> feedbackPage;
        if (userId != null) {
            List<Feedback> userFeedbacks = feedbackService.getLatestFeedbackByUser(userId);

            feedbackPage = feedbackService.getFeedbackPageExcludeUser(userId, pageable);
            model.addAttribute("feedbacks", feedbackPage);
            model.addAttribute("userFeedbacks", userFeedbacks);
        } else {
            feedbackPage = feedbackService.getFeedbackPage(pageable);
            model.addAttribute("feedbacks", feedbackPage);
        }
        if (page > feedbackPage.getTotalPages() && feedbackPage.getTotalPages() > 0) {
            return "redirect:/home?page=" + feedbackPage.getTotalPages();
        }
        model.addAttribute("feedbacks", feedbackPage);
        return "home/HomeGuest";
    }
}
