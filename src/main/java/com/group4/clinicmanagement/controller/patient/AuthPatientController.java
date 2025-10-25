package com.group4.clinicmanagement.controller.patient;

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
@RequestMapping("/auth")
public class AuthPatientController {

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private RegistrationService registrationService;

    // ===================== FORGOT PASSWORD =====================
    @GetMapping("/forgot-password")
    public String showForgotForm() {
        return "auth/patient/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String sendReset(@Valid @RequestParam(value = "email", required = false) String email, Model model) {
        try {
            passwordResetService.sendResetLink(email);
            model.addAttribute("success", "OTP sent to your email.");
            model.addAttribute("email", email);
            return "auth/patient/verify-otp";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/patient/forgot-password";
        }
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("email") String email,
                            @RequestParam(value = "otp", required = false) String otp,
                            Model model) {
        if (otp == null || otp.trim().isEmpty()) {
            model.addAttribute("error", "Please enter your OTP code.");
            model.addAttribute("email", email);
            return "auth/patient/verify-otp";
        }

        try {
            boolean valid = registrationService.verifyOtp(email, otp);
            if (!valid) {
                model.addAttribute("error", "Invalid or expired OTP!");
                model.addAttribute("email", email);
                return "auth/patient/verify-otp";
            }

            // ✅ OTP đúng → sang form tạo mật khẩu
            var user = passwordResetService.getUserByEmail(email);
            model.addAttribute("email", email);
            model.addAttribute("username", user.getUsername());
            model.addAttribute("email", email);
            model.addAttribute("setPassword", new SetPasswordDTO());
            return "auth/patient/create-password";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("email", email);
            return "auth/patient/verify-otp";
        }
    }

    // ===================== REGISTRATION FLOW =====================
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("patient", new PatientRegisterDTO());
        return "auth/patient/register-patient";
    }

    /**
     * 🟢 Bước 1: Đăng ký → Gửi OTP
     */
    @PostMapping("/register")
    public String registerPatient(
            @Valid @ModelAttribute("patient") PatientRegisterDTO dto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "auth/patient/register-patient";
        }

        try {
            registrationService.createPendingAccount(dto);
            model.addAttribute("success", "OTP has been sent to your email for verification.");
            model.addAttribute("email", dto.getEmail());
            return "auth/patient/verify-otp";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/patient/login";
        }
    }

    /**
     * 🟢 Bước 3: Tạo mật khẩu & kích hoạt tài khoản
     */
    @PostMapping("/create-password")
    public String createPassword(
            @ModelAttribute("setPassword") @Valid SetPasswordDTO dto,
            BindingResult result,
            @RequestParam("email") String email,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("email", email);
            return "auth/patient/create-password";
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            model.addAttribute("error", "Passwords do not match!");
            model.addAttribute("email", email);
            return "auth/patient/create-password";
        }

        try {
            registrationService.createAccountAfterOtp(email, dto.getPassword());
            model.addAttribute("success", "Account created successfully! You can now log in.");
            return "redirect:/patient/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("email", email);
            return "auth/patient/create-password";
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
        return "auth/patient/verify-otp";
    }
}
