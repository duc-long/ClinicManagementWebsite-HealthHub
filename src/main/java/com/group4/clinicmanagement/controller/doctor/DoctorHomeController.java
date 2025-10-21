package com.group4.clinicmanagement.controller.doctor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DoctorHomeController {
    @RequestMapping({"/doctor/home"})
    public String home(Model model) {
        return "doctor/home";
    }
}
