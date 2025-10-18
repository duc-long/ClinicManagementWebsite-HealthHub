package com.group4.clinicmanagement.controller;

import com.group4.clinicmanagement.dto.*;
import com.group4.clinicmanagement.service.LabService;
import com.group4.clinicmanagement.service.MedicalRecordService;
import com.group4.clinicmanagement.service.PatientService;
import com.group4.clinicmanagement.service.PrescriptionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/patient")
public class PatientController {
    @Autowired
    private PatientService patientService;

    @Autowired
    private MedicalRecordService medicalRecordService;

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private LabService labService;

    @GetMapping("/profile")
    public String getPatientsByUsername(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            session.setAttribute("username", "patient.tuan");
//            return "redirect:/login";
        }
        PatientUserDTO patient = patientService.getPatientsByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        session.setAttribute("patientId", patient.getPatientId());
        List<MedicalRecordListDTO> medicalRecords = medicalRecordService.getMedicalRecordsByPatientId(patient.getPatientId());

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
    public String updateProfile(@ModelAttribute("patient") PatientUserDTO dto, @RequestParam("avatar") MultipartFile avatar, HttpSession session) {
        String sessionUsername = (String) session.getAttribute("username");

        if (sessionUsername == null) {
            return "redirect:/login";
        }

        patientService.savePatientUserWithAvatar(sessionUsername, dto, avatar);
        return "redirect:/patient/profile";
    }

    @GetMapping("/medical-records/{recordId}")
    public String getMedicalRecordsByPatientId(Model model, HttpSession session, @PathVariable("recordId") Integer recordId) {
        Integer patientId = (Integer) session.getAttribute("patientId");
        if (patientId == null) {
            return "redirect:/login";
        }
        Optional<MedicalRecordDetailDTO> medicalRecordDetailDTO = medicalRecordService.getMedicalRecordDetailsByPatientId(patientId, recordId);
        List<PrescriptionDetailDTO> prescriptions = prescriptionService.getPrescriptionDetailsByRecordId(recordId);
        List<LabDTO> labs = labService.findLabResultByRecordId(recordId);
        model.addAttribute("prescriptions", prescriptions);
        model.addAttribute("labs", labs);
        model.addAttribute("record", medicalRecordDetailDTO.get());
        return "patient/medical-record-detail";
    }

}
