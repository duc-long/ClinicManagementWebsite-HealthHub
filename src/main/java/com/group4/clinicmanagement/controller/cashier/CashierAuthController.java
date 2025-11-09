package com.group4.clinicmanagement.controller.cashier;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cashier")
public class CashierAuthController {
    @GetMapping("/login")
    public String receptionistLogin(Model model, HttpSession session, @RequestParam(value = "error", required = false) String error){
        if (error == null) { // chỉ xoá khi không có lỗi
            session.removeAttribute("LOGIN_ERROR_MESSAGE");
        }
        return "auth/cashier/login";
    }
}
