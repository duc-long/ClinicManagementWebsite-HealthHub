package com.group4.clinicmanagement.controller;

import com.group4.clinicmanagement.entity.Doctor;

import com.group4.clinicmanagement.service.DoctorService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.List;

@Controller
@RequestMapping(value = "/healthhub")
public class HomeController {

    private final DoctorService doctorService;

    public HomeController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping(value = "/home")
    public String guestHome(Model model) {
        return "home/HomeGuest";
    }

    @GetMapping(value = "/list-doctor")
    public String listDoctor(Model model) {
        List<Doctor> doctors = doctorService.findAllDoctors();
        model.addAttribute("doctors", doctors);
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
        List<Doctor> doctors= doctorService.findDoctorByNameAndSpecialty(doctorName, specialty);
        model.addAttribute("doctors", doctors);
        List<String> specialties = doctorService.findAllDistinctSpecialties();
        model.addAttribute("specialties", specialties);
        model.addAttribute("pageTitle", "Search Results" +  "(" + doctors.size() + " doctors found)");
        return "home/doctor-search";
    }

    @PostMapping(value = "/view-details-doctor")
    public String viewDetailDoctor(Model model, @RequestParam(name = "doctorId") String doctorId) {
        return "home/doctor-view-details-doctor";
    }
}
