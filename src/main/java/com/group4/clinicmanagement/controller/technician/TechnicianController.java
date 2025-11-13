package com.group4.clinicmanagement.controller.technician;

import com.group4.clinicmanagement.dto.TechnicianDTO;
import com.group4.clinicmanagement.entity.Staff;
import com.group4.clinicmanagement.security.CustomUserDetails;
import com.group4.clinicmanagement.service.LabRequestService;
import com.group4.clinicmanagement.service.LabResultService;
import com.group4.clinicmanagement.service.TechnicianService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/technician")
public class TechnicianController {

    private final TechnicianService technicianService;
    private final LabRequestService labRequestService;
    private final LabResultService labResultService;
    private final PasswordEncoder passwordEncoder;

    public TechnicianController(TechnicianService technicianService,
                                LabRequestService labRequestService,
                                LabResultService labResultService,
                                PasswordEncoder passwordEncoder) {
        this.technicianService = technicianService;
        this.labRequestService = labRequestService;
        this.labResultService = labResultService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/technician/login";
    }

    @GetMapping("/profile")
    public String viewProfile(Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        try {
            CustomUserDetails customUser = (CustomUserDetails) authentication.getPrincipal();
            Staff user = technicianService.findByUserId(customUser.getUserId());

            TechnicianDTO technicianDTO = TechnicianDTO.fromEntity(user);
            model.addAttribute("technicianDTO", technicianDTO);

            return "technician/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Unexpected Error");
            return "redirect:/technician/profile";
        }

    }

    @GetMapping("/edit-profile")
    public String editProfile(Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Integer userId = userDetails.getUserId();

            Staff user = technicianService.findByUserId(userId);
            TechnicianDTO technicianDTO = TechnicianDTO.fromEntity(user);

            model.addAttribute("technicianDTO", technicianDTO);
            return "technician/edit-profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Unexpected Error");
            return "redirect:/technician/profile";
        }

    }

    @PostMapping("/edit-profile")
    public String editProfile(@Valid @ModelAttribute("technicianDTO") TechnicianDTO technicianDTO,
                              BindingResult bindingResult,
                              @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) throws IOException {
        try {
            if (bindingResult.hasErrors()) {
                return "technician/edit-profile";
            }

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Integer userId = userDetails.getUserId();

            technicianService.updateProfile(userId, technicianDTO, avatarFile);
            return "redirect:/technician/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Unexpected Error");
            return "redirect:/technician/profile";
        }
    }

    @GetMapping("/change-password")
    public String changePasswordForm(RedirectAttributes redirectAttributes) {
        try {
            return "technician/change-password";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Unexpected Error");
            return "redirect:/technician/profile";
        }
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Authentication authentication,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Integer userId = userDetails.getUserId();

            if(!passwordEncoder.matches(currentPassword, userDetails.getPassword())){
                redirectAttributes.addFlashAttribute("errorMessage", "Current Password Doesn't Match");
                return "redirect:/technician/change-password";
            }
            if(!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", "New Password Doesn't Match With Confirm Password");
                return "redirect:/technician/change-password";
            }

            boolean success = technicianService.changePassword(userId, currentPassword, newPassword, confirmPassword);

            if (success) {
                model.addAttribute("success", "Password changed successfully!");
            } else {
                model.addAttribute("error", "Failed to change password.");
            }

            return "auth/technician/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Unexpected Error");
            return "redirect:/technician/profile";
        }
    }
}
