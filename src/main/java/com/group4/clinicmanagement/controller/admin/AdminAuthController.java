package com.group4.clinicmanagement.controller.admin;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminAuthController {

    @GetMapping("/login")
    public String doctorLoginPage(HttpSession session, Model model, @RequestParam(value = "error", required = false) String error) {
        if (error == null) {
            session.removeAttribute("LOGIN_ERROR_MESSAGE");
        }
        return "auth/admin/login";
    }
}
