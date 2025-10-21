package com.group4.clinicmanagement.controller.patient;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/patient")
public class PatientAuthController {
    @GetMapping("/login")
    public String patientLoginPage(HttpSession session, @RequestParam(value = "error", required = false) String error) {
        if (error == null) { // chỉ xoá khi không có lỗi
            session.removeAttribute("LOGIN_ERROR_MESSAGE");
        }
        return "auth/patient/login";
    }

}
