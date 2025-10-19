package com.group4.clinicmanagement.controller.doctor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {
    @GetMapping("/patient")
    public String loginPatient() {
        return "auth/patient/login";
    }

    @GetMapping("/doctor")
    public String loginDoctor() {
        return "auth/doctor/login";
    }

    @GetMapping("/admin")
    public String loginAdmin()  {
        return "auth/admin/login";
    }

    @GetMapping("/cashier")
    public String loginCashier() {
        return "auth/cashier/login";
    }

    @GetMapping("/receptionist")
    public String loginReceptionist() {
        return "auth/receptionist/login";
    }
}
