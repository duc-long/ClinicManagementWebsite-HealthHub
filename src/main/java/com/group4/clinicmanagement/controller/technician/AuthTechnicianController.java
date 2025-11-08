package com.group4.clinicmanagement.controller.technician;

import com.group4.clinicmanagement.dto.registerpatient.PatientRegisterDTO;
import com.group4.clinicmanagement.dto.registerpatient.SetPasswordDTO;
import com.group4.clinicmanagement.service.PasswordResetService;
import com.group4.clinicmanagement.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/technician")
public class AuthTechnicianController {

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private RegistrationService registrationService;

    // ===================== FORGOT PASSWORD =====================
    @GetMapping("/forget-password")
    public String showForgotForm() {
        return "auth/technician/forget-password";
    }

    @PostMapping("/forget-password")
    public String sendReset(@Valid @RequestParam(value = "email", required = false) String email, Model model) {
        try {
            passwordResetService.sendResetLink(email);
            model.addAttribute("success", "OTP sent to your email.");
            model.addAttribute("email", email);
            return "auth/technician/verify-otp";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/technician/forget-password";
        }
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("email") String email,
                            @RequestParam(value = "otp", required = false) String otp,
                            Model model) {
        if (otp == null || otp.trim().isEmpty()) {
            model.addAttribute("error", "Please enter your OTP code.");
            model.addAttribute("email", email);
            return "auth/technician/verify-otp";
        }

        try {
            boolean valid = registrationService.verifyOtp(email, otp);
            if (!valid) {
                model.addAttribute("error", "Invalid or expired OTP!");
                model.addAttribute("email", email);
                return "auth/technician/verify-otp";
            }

            // ✅ OTP đúng → sang form tạo mật khẩu
            var user = passwordResetService.getUserByEmail(email);
            model.addAttribute("email", email);
            model.addAttribute("username", user.getUsername());
            model.addAttribute("email", email);
            model.addAttribute("setPassword", new SetPasswordDTO());
            return "auth/technician/create-password";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("email", email);
            return "auth/technician/verify-otp";
        }
    }

    // ===================== REGISTRATION FLOW =====================
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("patient", new PatientRegisterDTO());
        return "auth/patient/register-patient";
    }

    @PostMapping("/create-password")
    public String createPassword(
            @ModelAttribute("setPassword") @Valid SetPasswordDTO dto,
            BindingResult result,
            @RequestParam("email") String email,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("email", email);
            return "auth/technician/create-password";
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            model.addAttribute("error", "Passwords do not match!");
            model.addAttribute("email", email);
            return "auth/technician/create-password";
        }

        try {
            registrationService.createAccountAfterOtp(email, dto.getPassword());
            model.addAttribute("success", "Account created successfully! You can now log in.");
            return "redirect:/technician/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("email", email);
            return "auth/technician/create-password";
        }
    }

    @PostMapping("/resend-otp")
    public String resendOtp(@RequestParam("email") String email, Model model) {
        try {
            registrationService.resendOtp(email);
            model.addAttribute("success", "A new OTP has been sent to your email.");
            model.addAttribute("email", email);
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "auth/technician/verify-otp";
    }
}
