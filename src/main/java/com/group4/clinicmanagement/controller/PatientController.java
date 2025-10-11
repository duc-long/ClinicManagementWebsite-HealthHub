package com.group4.clinicmanagement.controller;

import com.group4.clinicmanagement.dto.PatientUserDTO;
import com.group4.clinicmanagement.service.PatientService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/patient")
public class PatientController {
    @Autowired
    private PatientService patientService;

    @GetMapping("/profile")
    public String getPatientsByUsername(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            session.setAttribute("username", "michael.t");
//            return "redirect:/login";
        }
        model.addAttribute("patient", patientService.getPatientsByUsername(username).orElseThrow(() -> new RuntimeException("User not found")));
        return "patient/profile";
    }

    @GetMapping("/edit-profile")
    public String goToEditProfile(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            session.setAttribute("username", "michael.t");
//            return "redirect:/login";
        }
        PatientUserDTO dto = patientService.getPatientsByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        ;
        model.addAttribute("patient", dto);
        return "patient/edit-profile";
    }

    @PostMapping("/save-profile")
    public String updateProfile(@ModelAttribute("patient") PatientUserDTO dto, @RequestParam("avatar") MultipartFile avatar, HttpSession session) {
        String sessionUsername = (String) session.getAttribute("username");

        if (sessionUsername == null) {
            return "redirect:/login";
        }

        patientService.savePatientUserWithAvatar(sessionUsername, dto, avatar);
        return "redirect:/patient/profile";
    }

}
