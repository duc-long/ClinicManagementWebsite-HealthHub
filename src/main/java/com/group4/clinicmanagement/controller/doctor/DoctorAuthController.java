package com.group4.clinicmanagement.controller.doctor;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/doctor")
public class DoctorAuthController {

    @GetMapping("/login")
    public String doctorLoginPage(HttpSession session, Model model, @RequestParam(value = "error", required = false) String error) {
        if (error == null) { // chỉ xoá khi không có lỗi
            session.removeAttribute("LOGIN_ERROR_MESSAGE");
        }
        return "auth/doctor/login";
    }
}
