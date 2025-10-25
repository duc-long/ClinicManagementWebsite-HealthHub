package com.group4.clinicmanagement.controller.admin;

import com.group4.clinicmanagement.dto.admin.PatientDTO;
import com.group4.clinicmanagement.entity.Patient;
import com.group4.clinicmanagement.service.UserService;
import com.group4.clinicmanagement.service.admin.PatientForAdminService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;


@Controller
@RequestMapping(value = "/admin")
public class AdminController {
    private final PatientForAdminService patientService;
    private final UserService userService;
    public AdminController(PatientForAdminService patientService, UserService userService) {
        this.patientService = patientService;
        this.userService = userService;
    }

    @GetMapping(value = "/patient")
    public String showPatientList(Model model,
                              @RequestParam(value = "size", defaultValue = "10") Integer size,
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
    public String editPatientResult(@Valid @ModelAttribute(name = "patientDTO") PatientDTO dto,
                                    BindingResult bindingResult,
                                    @RequestParam("avatar") MultipartFile avatar,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("patientDTO", dto);
            model.addAttribute("today", LocalDate.now());
            return "admin/update-patient";
        }

        try {
            patientService.update(dto, avatar);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Patient with ID: " + dto.getPatientId() + " was updated successfully!");
            return "redirect:/admin/patient";
        } catch (Exception e) {
            model.addAttribute("today", LocalDate.now());
            model.addAttribute("patientDTO", dto);
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
    public String deletePatientById(@ModelAttribute(name = "patientDTO") PatientDTO dto, Model model, RedirectAttributes redirectAttributes) {
        try {
            patientService.deletePatient(dto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Patient with ID: " + dto.getPatientId() + " was deleted successfully!");
            return "redirect:/admin/patient";
        } catch (Exception e) {
            System.out.println(  "\n" + "Error: " + e.getMessage() + "\n");
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Cannot delete the patient with ID: " + dto.getPatientId());
            return "redirect:/admin/patient";
        }
    }

    @GetMapping(value = "/patient/new")
    public String addNewPatient(Model model) {
        model.addAttribute("patientDTO", new PatientDTO());
        return "admin/add-new-patient";
    }

    @PostMapping(value = "/patient/new-result")
    public String addNewPatientResult(
            @Valid @ModelAttribute("patientDTO") PatientDTO dto,
            BindingResult bindingResult,
            @RequestParam("avatar") MultipartFile avatar,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (userService.isUsernameDuplicate(dto.getUsername())) {
            bindingResult.rejectValue("username", "error.username", "Username already exists");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("patientDTO", dto);
            model.addAttribute("today", LocalDate.now());
            System.out.printf("%s\n", bindingResult.getAllErrors());
            return "admin/add-new-patient";
        } else {
            Patient patient = patientService.newPatient(dto, avatar);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Patient with ID: " + patient.getPatientId() + " was created successfully!");
            return "redirect:/admin/patient";
        }


    }



}
