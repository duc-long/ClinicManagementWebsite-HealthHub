package com.group4.clinicmanagement.controller.patient;

import com.group4.clinicmanagement.dto.MedicalRecordListDTO;
import com.group4.clinicmanagement.dto.PatientUserDTO;
import com.group4.clinicmanagement.service.MedicalRecordListService;
import com.group4.clinicmanagement.service.PatientService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/patient")
public class PatientController {
    @Autowired
    private PatientService patientService;

    @Autowired
    private MedicalRecordListService medicalRecordListService;

    @GetMapping("/profile")
    public String getPatientsByUsername(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            session.setAttribute("username", "patient.jane");
//            return "redirect:/login";
        }
        PatientUserDTO patient = patientService.getPatientsByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        List<MedicalRecordListDTO> medicalRecords = medicalRecordListService.getMedicalRecordsByPatientId(patient.getPatientId());

        model.addAttribute("patient", patient);
        model.addAttribute("medicalRecords", medicalRecords);
        return "patient/profile";
    }

    @GetMapping("/edit-profile")
    public String goToEditProfile(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            session.setAttribute("username", "patient.jane");
//            return "redirect:/login";
        }

        PatientUserDTO dto = patientService.getPatientsByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("patient", dto);
        return "patient/edit-profile";
    }

    @PostMapping("/save-profile")
    public String updateProfile(@Valid @ModelAttribute("patient") PatientUserDTO dto,  BindingResult result,@RequestParam("avatar") MultipartFile avatar, HttpSession session,
                                Model model) {
        String sessionUsername = (String) session.getAttribute("username");

        if (sessionUsername == null) {
            return "redirect:/login";
        }
        if (!result.hasFieldErrors("fullName") && dto.getFullName() == null) {
            result.rejectValue("fullName", "missing", "Full Name bị thiếu");
        }
        if (result.hasErrors()) {
            model.addAttribute("patient", dto);
            return "patient/edit-profile"; // trả lại form với lỗi
        }

        patientService.savePatientUserWithAvatar(sessionUsername, dto, avatar);
        return "redirect:/patient/profile";
    }

}
