package com.group4.clinicmanagement.controller.admin;

import com.group4.clinicmanagement.dto.admin.PatientDTO;
import com.group4.clinicmanagement.service.admin.PatientForAdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping(value = "/admin")
public class AdminController {
    private final PatientForAdminService patientService;
    public AdminController(PatientForAdminService patientService) {
        this.patientService = patientService;
    }

    @GetMapping(value = "/patient")
    public String showPatientList(Model model,
                              @RequestParam(value = "size", defaultValue = "5") Integer size,
                              @RequestParam(value = "page", defaultValue = "0") Integer page) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PatientDTO> patientDTOs = patientService.findAll(pageable);
        model.addAttribute("patientDTOs", patientDTOs);
        return "admin/manage-patients-for-admin";
    }

    @GetMapping(value = "/patient/{id}")
    public String showPatientById(@PathVariable(value = "id") Integer id, Model model) {
        PatientDTO patientDTO = patientService.findById(id);
        model.addAttribute("patientDTO", patientDTO);
        return "admin/patient-details";
    }

    @GetMapping(value = "/patient/edit/{id}")
    public String editPatientById(@PathVariable(value = "id") Integer id, Model model) {
        PatientDTO patientDTO = patientService.findById(id);
        model.addAttribute("today", java.time.LocalDate.now());
        model.addAttribute("patientDTO", patientDTO);
        model.addAttribute("error", "");
        return "admin/update-patient";
    }

    @PostMapping(value = "/patient/edit-result")
    public String editPatientResult(@ModelAttribute(name = "patientDTO") PatientDTO dto, @RequestParam("avatar") MultipartFile avatar , Model model) {
        try {
            patientService.update(dto, avatar);
            return  "redirect:/admin/patient";
        }  catch (Exception e) {
            model.addAttribute("today", java.time.LocalDate.now());
            model.addAttribute("error", e.getMessage());
            return "admin/update-patient";
        }
    }

    @GetMapping(value = "/patient/delete/{id}")
    public String deletePatientById(@PathVariable(value = "id") Integer id, Model model) {
        PatientDTO patientDTO = patientService.findById(id);
        model.addAttribute("patientDTO", patientDTO);
        return "admin/delete-patient";
    }

    @PostMapping(value = "/patient/delete-result")
    public String deletePatientById(@ModelAttribute(name = "patientDTO") PatientDTO dto, Model model) {
        try {
            patientService.deletePatient(dto);
            return "redirect:/admin/patient";
        } catch (Exception e) {
            model.addAttribute("today", java.time.LocalDate.now());
            model.addAttribute("error", e.getMessage());
            return "admin/delete-patient";
        }
    }

}
