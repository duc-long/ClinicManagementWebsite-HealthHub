package com.group4.clinicmanagement.controller.patient;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PatientAuthController {
    @GetMapping({"/patient/login", "/login"})
    public String patientLoginPage(Model model, HttpSession session, @RequestParam(value = "error", required = false) String error) {
        if (error == null) { // chỉ xoá khi không có lỗi
            session.removeAttribute("LOGIN_ERROR_MESSAGE");
        }
        return "auth/patient/login";
    }
}
