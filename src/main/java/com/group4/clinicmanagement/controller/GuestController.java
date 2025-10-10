package com.group4.clinicmanagement.controller;

import com.group4.clinicmanagement.entity.Doctor;
import com.group4.clinicmanagement.repository.DoctorRepositories;
import com.group4.clinicmanagement.service.DoctorService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping(value = "/guest")
public class GuestController {

    private final DoctorService doctorService;

    public GuestController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public String guestHome(Model model) {
        return "guest/HomeGuest";
    }

    @GetMapping(value = "/list-doctor")
    public String listDoctor(Model model) {
        List<Doctor> doctors = doctorService.findAllDoctors();
        model.addAttribute("doctors", doctors);
        return "guest/doctor-list";
    }
}
