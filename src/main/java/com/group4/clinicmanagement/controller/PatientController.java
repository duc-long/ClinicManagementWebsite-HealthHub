package com.group4.clinicmanagement.controller;

import com.group4.clinicmanagement.dto.PatientUserDTO;
import com.group4.clinicmanagement.service.PatientService;
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

    @GetMapping("/profile/{username}")
    public String getPatientsByUsername(Model model, @PathVariable("username") String username) {
        model.addAttribute("patient", patientService.getPatientsByUsername(username).get());
        patientService.getPatientsByUsername(username).stream().toList();
        return "patient/profile";
    }

    @PostMapping("/edit-profile")
    public String goToEditProfile(@RequestParam String username, Model model) {
        // Gọi lại service để load thông tin user cần sửa (nếu cần)
        PatientUserDTO dto = patientService.getPatientsByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        ;
        model.addAttribute("patient", dto);
        return "patient/edit-profile";
    }

    @PostMapping("/save-profile")
    public String updateProfile(@ModelAttribute("patient") PatientUserDTO dto, @RequestParam("avatar") MultipartFile avatar, RedirectAttributes back) {
        // Xử lý cập nhật
        patientService.savePatientUserWithAvatar(dto.getUsername(), dto, avatar);
        back.addAttribute("username", dto.getUsername());

        // Sau khi xong, trả về view "Profile.html"
        return "redirect:/patient/profile/{username}";
    }

}
