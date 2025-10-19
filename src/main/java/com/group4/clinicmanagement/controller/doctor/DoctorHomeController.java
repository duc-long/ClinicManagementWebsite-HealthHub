package com.group4.clinicmanagement.controller.doctor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DoctorHomeController {
    @RequestMapping({"/", "/doctor/overview"})
    public String home() {
        return "doctor/home";
    }
}
