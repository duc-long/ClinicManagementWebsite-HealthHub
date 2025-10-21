package com.group4.clinicmanagement.controller.admin;

import com.group4.clinicmanagement.dto.admin.PatientDTO;
import com.group4.clinicmanagement.service.admin.PatientForAdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping(value = "/admin")
public class AdminController {
    private final PatientForAdminService patientService;
    public AdminController(PatientForAdminService patientService) {
        this.patientService = patientService;
    }

    @GetMapping(value = "/patient")
    public String showPatient(Model model,
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

}
