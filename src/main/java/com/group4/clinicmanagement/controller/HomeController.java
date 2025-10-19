package com.group4.clinicmanagement.controller;

import com.group4.clinicmanagement.dto.FeedbackDTO;
import com.group4.clinicmanagement.entity.Appointment;
import com.group4.clinicmanagement.entity.Doctor;
import com.group4.clinicmanagement.entity.Feedback;
import com.group4.clinicmanagement.service.DoctorService;
import com.group4.clinicmanagement.service.FeedbackService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/home")
public class HomeController {

    private final DoctorService doctorService;
    private final FeedbackService feedbackService;

    public HomeController(DoctorService doctorService, FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
        this.doctorService = doctorService;
    }

    @GetMapping
    public String guestHome(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
//            session.setAttribute("username", "patient.jane");
        }
        List<Doctor> doctors = doctorService.findAllDoctors();
        model.addAttribute("doctors", doctors);
        List<String> specialties = doctorService.findAllDistinctSpecialties();
        model.addAttribute("specialties", specialties);
        List<Feedback> feedbacks = feedbackService.getRecentFeedbacks();
        model.addAttribute("feedbacks", feedbacks);
        double averageRating = feedbackService.getAverageRating();  // Tính trung bình rating
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("feedbackDTO", new FeedbackDTO());
        List<Appointment> eligibleAppointments = feedbackService.getEligibleAppointmentsForFeedback(username);
        model.addAttribute("eligibleAppointments", eligibleAppointments);
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
