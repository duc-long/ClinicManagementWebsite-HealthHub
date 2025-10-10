package com.group4.clinicmanagement.controller;

import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.service.CashierService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CashierController {
    CashierService cashierService;

    public CashierController(CashierService cashierService) {
        this.cashierService = cashierService;
    }

    @GetMapping(value = "/cashier/cash-profile")
    public String viewProfile(int id, Model model) {
        User user = cashierService.findUserById(18);
        model.addAttribute("user", user);

        System.out.println(user.getFullName());
        return "cashier/cash-profile";
    }
}
