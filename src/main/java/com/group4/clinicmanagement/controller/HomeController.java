package com.group4.clinicmanagement.controller;

import com.group4.clinicmanagement.entity.Doctor;
import com.group4.clinicmanagement.service.DoctorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/home")
public class HomeController {

    private final DoctorService doctorService;

    public HomeController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public String guestHome(Model model, HttpSession session) {
        List<Doctor> doctors = doctorService.findAllDoctors();
        model.addAttribute("doctors", doctors);
        List<String> specialties = doctorService.findAllDistinctSpecialties();
        session.setAttribute("specialties", specialties);
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
