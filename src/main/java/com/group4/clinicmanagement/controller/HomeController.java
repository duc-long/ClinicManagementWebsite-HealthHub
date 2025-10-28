package com.group4.clinicmanagement.controller;

import com.group4.clinicmanagement.entity.Appointment;
import com.group4.clinicmanagement.entity.Doctor;
import com.group4.clinicmanagement.entity.Feedback;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.security.CustomUserDetails;
import com.group4.clinicmanagement.service.DoctorService;
import com.group4.clinicmanagement.service.FeedbackService;
import com.group4.clinicmanagement.service.UserService;
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

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping(value = "/home")
public class HomeController {

    private final DoctorService doctorService;
    private final FeedbackService feedbackService;
    private final UserService userService;

    public HomeController(DoctorService doctorService, FeedbackService feedbackService, UserService userService) {
        this.feedbackService = feedbackService;
        this.doctorService = doctorService;
        this.userService = userService;
    }

    @GetMapping()
    public String guestHome(Model model, @RequestParam(defaultValue = "1") int page, HttpSession session, Principal principal) {
        User user = userService.findUserByUsername(principal.getName());
        if (!user.getRole().getName().equals("Patient")) {
            return "redirect:/patient/login";
        }

        int pageSize = 3; // hiển thị 3 feedback mỗi trang
        if (page < 1) {
            return "redirect:/home?page=1";
        }
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());

        List<Doctor> doctors = doctorService.findAllDoctors();
        model.addAttribute("doctors", doctors);
        List<String> specialties = doctorService.findAllDistinctSpecialties();
        session.setAttribute("specialties", specialties);
        double averageRating = feedbackService.getAverageRating();
        model.addAttribute("averageRating", averageRating);


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            userId = userDetails.getUserId(); // lấy từ CustomUserDetails

            List<Appointment> eligibleAppointments = feedbackService.getEligibleAppointmentsForFeedback(userId);
            model.addAttribute("eligibleAppointments", eligibleAppointments);
        }

        Page<Feedback> feedbackPage;
        if (userId != null) {
            List<Feedback> userFeedbacks = feedbackService.getLatestFeedbackByUser(userId);

            feedbackPage = feedbackService.getFeedbackPageExcludeUser(userId, pageable);
            model.addAttribute("feedbacks", feedbackPage);
            model.addAttribute("userFeedbacks", userFeedbacks); // hiển thị riêng phần đầu
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

    @GetMapping(value = "/list-doctor")
    public String listDoctor(Model model) {
        List<Doctor> doctors = doctorService.findAllDoctors();
        model.addAttribute("doctors", doctors);
        List<String> specialties = doctorService.findAllDistinctSpecialties();
        model.addAttribute("specialties", specialties);
        return "home/doctor-list";
    }

    @GetMapping(value = "/search-doctor")
    public String searchDoctor(Model model) {
        List<String> specialties = doctorService.findAllDistinctSpecialties();
        List<Doctor> doctors = null;
        model.addAttribute("doctors", null);
        model.addAttribute("specialties", specialties);
        return "home/doctor-search";
    }

    @PostMapping(value = "/search-doctor")
    public String searchDoctor(@RequestParam(name = "specialty") String specialty,
                               @RequestParam(name = "doctorName") String doctorName,
                               Model model) {
        List<Doctor> doctors = doctorService.findDoctorByNameAndSpecialty(doctorName, specialty);
        model.addAttribute("doctors", doctors);
        List<String> specialties = doctorService.findAllDistinctSpecialties();
        model.addAttribute("specialties", specialties);
        model.addAttribute("pageTitle", "Search Results" + "(" + doctors.size() + " doctors found)");
        return "home/doctor-search";
    }

    @GetMapping(value = "/doctor-profile/{doctorId}")
    public String viewDetailDoctor(Model model, @PathVariable(name = "doctorId") int doctorId) {
        Doctor doctor = doctorService.findDoctorById(doctorId);
        model.addAttribute("doctor", doctor);
        List<String> specialties = doctorService.findAllDistinctSpecialties();
        model.addAttribute("specialties", specialties);
        return "home/doctor-profile";
    }

    @GetMapping(value = "/specialty/{specialty}")
    public String viewDoctorSpecialty(Model model, @PathVariable(name = "specialty") String specialty) {
        List<Doctor> doctors = doctorService.getDoctorBySpecialtyIgnoreCase(specialty);
        model.addAttribute("doctors", doctors);
        List<String> specialties = doctorService.findAllDistinctSpecialties();
        model.addAttribute("specialties", specialties);
        model.addAttribute("specialty", specialty);
        return "home/department-doctor-list";
    }

}
