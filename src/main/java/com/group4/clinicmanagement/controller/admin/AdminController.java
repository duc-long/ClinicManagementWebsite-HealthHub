package com.group4.clinicmanagement.controller.admin;

import com.group4.clinicmanagement.dto.DepartmentDTO;
import com.group4.clinicmanagement.dto.admin.DoctorDTO;
import com.group4.clinicmanagement.dto.admin.PatientDTO;
import com.group4.clinicmanagement.dto.admin.ReceptionistDTO;
import com.group4.clinicmanagement.repository.admin.DoctorForAdminRepository;
import com.group4.clinicmanagement.service.DepartmentService;
import com.group4.clinicmanagement.service.UserService;
import com.group4.clinicmanagement.service.admin.DoctorForAdminService;
import com.group4.clinicmanagement.service.admin.PatientForAdminService;
import com.group4.clinicmanagement.service.admin.ReceptionistForAdminService;
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
import java.util.List;


@Controller
@RequestMapping(value = "/admin")
public class AdminController {
    private final PatientForAdminService patientService;
    private final DoctorForAdminService doctorService;
    private final UserService userService;
    private final DepartmentService departmentService;
    private final DoctorForAdminRepository doctorForAdminRepository;
    private final ReceptionistForAdminService receptionistService;

    public AdminController(PatientForAdminService patientService, DoctorForAdminService doctorService, UserService userService, DepartmentService departmentService, DoctorForAdminRepository doctorForAdminRepository, ReceptionistForAdminService receptionistService) {
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.userService = userService;
        this.departmentService = departmentService;
        this.doctorForAdminRepository = doctorForAdminRepository;
        this.receptionistService = receptionistService;
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
    public String deletePatientById(@ModelAttribute(name = "patientDTO") PatientDTO dto, RedirectAttributes redirectAttributes) {
        try {
            patientService.deletePatient(dto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Patient with ID: " + dto.getPatientId() + " was deleted successfully!");
            return "redirect:/admin/patient";
        } catch (Exception e) {
            System.out.println("\n" + "Error: " + e.getMessage() + "\n");
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
            try {
                Integer patientId = patientService.newPatient(dto, avatar);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Patient with ID: " + patientId + " was created successfully!");
                return "redirect:/admin/patient";
            } catch (Exception e) {
                System.out.println(e.getMessage() + "Error ------------------------------\n");
                return "admin/add-new-patient";
            }

        }


    }

    @GetMapping(value = "/doctor")
    public String showDoctorList(Model model,
                                 @RequestParam(value = "size", defaultValue = "10") Integer size,
                                 @RequestParam(value = "page", defaultValue = "0") Integer page) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DoctorDTO> doctorDTOS = doctorService.findAllDoctors(pageable);
        model.addAttribute("doctorDTOS", doctorDTOS);
        return "admin/manage-doctors-for-admin";
    }

    @GetMapping(value = "/doctor/{id}")
    public String showDoctorById(@PathVariable(value = "id") Integer id, Model model) {
        DoctorDTO doctorDTO = doctorService.findById(id);
        model.addAttribute("doctorDTO", doctorDTO);
        return "admin/doctor-details";
    }

    @GetMapping(value = "/doctor/edit/{id}")
    public String editDoctorById(@PathVariable(value = "id") Integer id, Model model) {
        DoctorDTO doctorDTO = doctorService.findById(id);
        List<DepartmentDTO> patientDTOList = departmentService.findAll();
        model.addAttribute("patientDTOList", patientDTOList);
        model.addAttribute("doctorDTO", doctorDTO);
        model.addAttribute("error", "");
        return "admin/update-doctor";
    }

    @PostMapping(value = "/doctor/edit-result")
    public String editDoctorResult(@Valid @ModelAttribute(name = "doctorDTO") DoctorDTO dto,
                                   BindingResult bindingResult,
                                   @RequestParam("avatar") MultipartFile avatar,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {

        if (doctorService.isLicenseNoDuplicateForUpdate(dto.getLicenseNo(), dto.getDoctorId())) {
            bindingResult.rejectValue("licenseNo", "error.licenseNo", "LicenseNo already exists");
        }
        if (bindingResult.hasErrors()) {
            List<DepartmentDTO> patientDTOList = departmentService.findAll();
            model.addAttribute("patientDTOList", patientDTOList);
            model.addAttribute("doctorDTO", dto);
            return "admin/update-doctor";
        }

        try {
            doctorService.update(dto, avatar);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Doctor with ID: " + dto.getDoctorId() + " was updated successfully!");
            return "redirect:/admin/doctor";
        } catch (Exception e) {
            List<DepartmentDTO> patientDTOList = departmentService.findAll();
            System.out.println(e.getMessage() + "Error ------------------------------\n");
            model.addAttribute("patientDTOList", patientDTOList);
            model.addAttribute("doctorDTO", dto);
            return "admin/update-doctor";
        }
    }

    @GetMapping(value = "/doctor/new")
    public String addNewDoctor(Model model) {
        List<DepartmentDTO> patientDTOList = departmentService.findAll();
        model.addAttribute("patientDTOList", patientDTOList);
        model.addAttribute("doctorDTO", new DoctorDTO());
        return "admin/add-new-doctor";
    }

    @PostMapping(value = "/doctor/new-result")
    public String addNewDoctorResult(
            @Valid @ModelAttribute("doctorDTO") DoctorDTO dto,
            BindingResult bindingResult,
            @RequestParam("avatar") MultipartFile avatar,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (doctorService.isLicenseNoDuplicateForNewDoctor(dto.getLicenseNo(), dto.getDoctorId())) {
            bindingResult.rejectValue("licenseNo", "error.licenseNo", "LicenseNo already exists");
        }
        if (userService.isUsernameDuplicate(dto.getUsername())) {
            bindingResult.rejectValue("username", "error.username", "Username already exists");
        }
        if (bindingResult.hasErrors()) {
            List<DepartmentDTO> patientDTOList = departmentService.findAll();
            model.addAttribute("patientDTOList", patientDTOList);
            model.addAttribute("doctorDTO", dto);
            return "admin/add-new-doctor";
        } else {
            try {
                Integer doctorId = doctorService.newDoctor(dto, avatar);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Doctor with ID: " + doctorId + " was created successfully!");
                return "redirect:/admin/doctor";
            } catch (Exception e) {
                System.out.println(e.getMessage() + "Error ------------------------------\n");
                List<DepartmentDTO> patientDTOList = departmentService.findAll();
                model.addAttribute("patientDTOList", patientDTOList);
                return "admin/add-new-doctor";
            }
        }
    }

    @GetMapping(value = "/doctor/delete/{id}")
    public String deleteDoctorById(@PathVariable(value = "id") Integer id, Model model) {
        DoctorDTO doctorDTO = doctorService.findById(id);
        model.addAttribute("doctorDTO", doctorDTO);
        return "admin/delete-doctor";
    }

    @PostMapping(value = "/doctor/delete-result")
    public String deleteDoctorById(@ModelAttribute(name = "doctorDTO") DoctorDTO dto, RedirectAttributes redirectAttributes) {
        try {
            doctorService.deleteDoctor(dto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Doctor with ID: " + dto.getDoctorId() + " was deleted successfully!");
            return "redirect:/admin/doctor";
        } catch (Exception e) {
            System.out.println("\n" + "Error: " + e.getMessage() + "\n");
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Cannot delete the doctor with ID: " + dto.getDoctorId());
            return "redirect:/admin/doctor";
        }
    }

    @GetMapping(value = "/receptionist")
    public String showReceptionistList(Model model,
                                  @RequestParam(value = "size", defaultValue = "10") Integer size,
                                  @RequestParam(value = "page", defaultValue = "0") Integer page) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReceptionistDTO> ReceptionistDTOs = receptionistService.findAll(pageable);
        model.addAttribute("receptionistDTOs", ReceptionistDTOs);
        return "admin/manage-receptionists-for-admin";
    }

    @GetMapping(value = "/receptionist/{id}")
    public String showReceptionistById(@PathVariable(value = "id") Integer id, Model model) {
        ReceptionistDTO ReceptionistDTO = receptionistService.findById(id);
        model.addAttribute("receptionistDTO", ReceptionistDTO);
        return "admin/receptionist-details";
    }

    @GetMapping(value = "/receptionist/edit/{id}")
    public String editReceptionistById(@PathVariable(value = "id") Integer id, Model model) {
        ReceptionistDTO receptionistDTO = receptionistService.findById(id);
        model.addAttribute("receptionistDTO", receptionistDTO);
        return "admin/update-receptionist";
    }

    @PostMapping(value = "/receptionist/edit-result")
    public String editReceptionistResult(@Valid @ModelAttribute(name = "receptionistDTO") ReceptionistDTO dto,
                                    BindingResult bindingResult,
                                    @RequestParam("avatar") MultipartFile avatar,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("receptionistDTO", dto);
            return "admin/update-receptionist";
        }
        try {
            receptionistService.update(dto, avatar);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Receptionist with ID: " + dto.getUserId() + " was updated successfully!");
            return "redirect:/admin/receptionist";
        } catch (Exception e) {
            model.addAttribute("receptionistDTO", dto);
            return "admin/update-receptionist";
        }
    }


    @GetMapping(value = "/receptionist/delete/{id}")
    public String deleteReceptionistById(@PathVariable(value = "id") Integer id, Model model) {
        ReceptionistDTO receptionistDTO = receptionistService.findById(id);
        model.addAttribute("receptionistDTO", receptionistDTO);
        return "admin/delete-receptionist";
    }

    @PostMapping(value = "/receptionist/delete-result")
    public String deleteReceptionistById(@ModelAttribute(name = "receptionistDTO") ReceptionistDTO dto, RedirectAttributes redirectAttributes) {
        try {
            receptionistService.delete(dto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Receptionist with ID: " + dto.getUserId() + " was deleted successfully!");
            return "redirect:/admin/receptionist";
        } catch (Exception e) {
            System.out.println("\n" + "Error: " + e.getMessage() + "\n");
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Cannot delete the receptionist with ID: " + dto.getUserId());
            return "redirect:/admin/receptionist";
        }
    }

    @GetMapping(value = "/receptionist/new")
    public String addNewReceptionist(Model model) {
        model.addAttribute("receptionistDTO", new ReceptionistDTO());
        return "admin/add-new-receptionist";
    }

    @PostMapping(value = "/receptionist/new-result")
    public String addNewReceptionistResult(
            @Valid @ModelAttribute("receptionistDTO") ReceptionistDTO dto,
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
            System.out.printf("%s\n", bindingResult.getAllErrors());
            return "admin/add-new-receptionist";
        } else {
            try {
                Integer receptionistId = receptionistService.newReceptionist(dto, avatar);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Receptionist with ID: " + receptionistId + " was created successfully!");
                return "redirect:/admin/receptionist";
            } catch (Exception e) {
                System.out.println(e.getMessage() + "Error ------------------------------\n");
                return "admin/add-new-receptionist";
            }

        }

    }
}
