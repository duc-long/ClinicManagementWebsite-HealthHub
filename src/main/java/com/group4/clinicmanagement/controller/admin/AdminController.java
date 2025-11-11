package com.group4.clinicmanagement.controller.admin;

import com.group4.clinicmanagement.dto.DepartmentDTO;
import com.group4.clinicmanagement.dto.admin.*;
import com.group4.clinicmanagement.entity.Feedback;
import com.group4.clinicmanagement.repository.admin.DoctorForAdminRepository;
import com.group4.clinicmanagement.service.BillService;
import com.group4.clinicmanagement.service.DepartmentService;
import com.group4.clinicmanagement.service.FeedbackService;
import com.group4.clinicmanagement.service.UserService;
import com.group4.clinicmanagement.service.admin.*;
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
    private final CashierForAdminService CashierService;
    private final TechnicianForAdminService TechnicianService;
    private final FeedbackService feedbackService;
    private final BillService billService;

    public AdminController(PatientForAdminService patientService, DoctorForAdminService doctorService, UserService userService, DepartmentService departmentService, DoctorForAdminRepository doctorForAdminRepository, ReceptionistForAdminService receptionistService, CashierForAdminService cashierService, TechnicianForAdminService technicianService, FeedbackService feedbackService, BillService billService) {
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.userService = userService;
        this.departmentService = departmentService;
        this.doctorForAdminRepository = doctorForAdminRepository;
        this.receptionistService = receptionistService;
        this.CashierService = cashierService;
        this.TechnicianService = technicianService;
        this.feedbackService = feedbackService;
        this.billService = billService;
    }

    @GetMapping(value = "/patient")
    public String showPatientList(Model model,
                                  @RequestParam(value = "size", defaultValue = "10") String size,
                                  @RequestParam(value = "page", defaultValue = "0") String page, RedirectAttributes redirectAttributes) {
        try {
            Integer pageC = Integer.parseInt(page);
            Integer sizeC = Integer.parseInt(size);
            Pageable pageable = PageRequest.of(pageC, sizeC);
            Page<PatientDTO> patientDTOs = patientService.findAll(pageable);
            model.addAttribute("patientDTOs", patientDTOs);
            return "admin/manage-patients-for-admin";
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Patient not found");
            return "redirect:/admin/patient";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Unexpected Error");
            return "redirect:/admin/patient";
        }
    }

    @GetMapping(value = "/patient/{id}")
    public String showPatientById(@PathVariable(value = "id") String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Integer idC = Integer.parseInt(id);
            PatientDTO patientDTO = patientService.findById(idC);
            model.addAttribute("patientDTO", patientDTO);
            return "admin/patient-details";
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Patient not found");
            return "redirect:/admin/patient";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Unexpected Error");
            return "redirect:/admin/patient";
        }
    }

    @GetMapping(value = "/patient/edit/{id}")
    public String editPatientById(@PathVariable(value = "id") String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Integer idC = Integer.parseInt(id);
            PatientDTO patientDTO = patientService.findById(idC);
            model.addAttribute("today", java.time.LocalDate.now());
            model.addAttribute("patientDTO", patientDTO);
            model.addAttribute("error", "");
            return "admin/update-patient";
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Patient not found");
            return "redirect:/admin/patient";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Unexpected Error");
            return "redirect:/admin/patient";
        }
    }

    @PostMapping(value = "/patient/edit-result")
    public String editPatientResult(@Valid @ModelAttribute(name = "patientDTO") PatientDTO dto,
                                    BindingResult bindingResult,
                                    @RequestParam("avatar") MultipartFile avatar,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        if (userService.isMailNoDuplicate(dto.getEmail(), dto.getUserId())) {
            bindingResult.rejectValue("email", "error.email", "Email already exists");
        }
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
    public String deletePatientById(@PathVariable(value = "id") String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Integer idC = Integer.parseInt(id);
            PatientDTO patientDTO = patientService.findById(idC);
            model.addAttribute("patientDTO", patientDTO);
            return "admin/delete-patient";
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Patient not found");
            return "redirect:/admin/patient";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Unexpected Error");
            return "redirect:/admin/patient";
        }
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
        if (userService.isMailNoDuplicate(dto.getEmail(), dto.getUserId())) {
            bindingResult.rejectValue("email", "error.email", "Email already exists");
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
                                 @RequestParam(value = "size", defaultValue = "10") String size,
                                 @RequestParam(value = "page", defaultValue = "0") String page, RedirectAttributes redirectAttributes) {
        try {
            Integer pageC = Integer.parseInt(page);
            Integer sizeC = Integer.parseInt(size);
            Pageable pageable = PageRequest.of(pageC, sizeC);
            Page<DoctorDTO> doctorDTOS = doctorService.findAllDoctors(pageable);
            model.addAttribute("doctorDTOS", doctorDTOS);
            return "admin/manage-doctors-for-admin";
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Doctor not found");
            return "redirect:/admin/doctor";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Unexpected Error");
            return "redirect:/admin/doctor";
        }
    }

    @GetMapping(value = "/doctor/{id}")
    public String showDoctorById(@PathVariable(value = "id") String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Integer idC = Integer.parseInt(id);
            DoctorDTO doctorDTO = doctorService.findById(idC);
            model.addAttribute("doctorDTO", doctorDTO);
            return "admin/doctor-details";
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Doctor not found");
            return "redirect:/admin/doctor";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Unexpected Error");
            return "redirect:/admin/doctor";
        }
    }

    @GetMapping(value = "/doctor/edit/{id}")
    public String editDoctorById(@PathVariable(value = "id") String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Integer idC = Integer.parseInt(id);
            DoctorDTO doctorDTO = doctorService.findById(idC);
            List<DepartmentDTO> patientDTOList = departmentService.findAll();
            model.addAttribute("patientDTOList", patientDTOList);
            model.addAttribute("doctorDTO", doctorDTO);
            model.addAttribute("error", "");
            return "admin/update-doctor";
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Doctor not found");
            return "redirect:/admin/doctor";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Unexpected Error");
            return "redirect:/admin/doctor";
        }
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
        if (userService.isMailNoDuplicate(dto.getEmail(), dto.getUserId())) {
            bindingResult.rejectValue("email", "error.email", "Email already exists");
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
        if (userService.isMailNoDuplicate(dto.getEmail(), dto.getUserId())) {
            bindingResult.rejectValue("email", "error.email", "Email already exists");
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
    public String deleteDoctorById(@PathVariable(value = "id") String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Integer idC = Integer.parseInt(id);
            DoctorDTO doctorDTO = doctorService.findById(idC);
            model.addAttribute("doctorDTO", doctorDTO);
            return "admin/delete-doctor";
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Doctor not found");
            return "redirect:/admin/doctor";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Unexpected Error");
            return "redirect:/admin/doctor";
        }
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
                                       @RequestParam(value = "size", defaultValue = "10") String size,
                                       @RequestParam(value = "page", defaultValue = "0") String page, RedirectAttributes redirectAttributes) {
        try {
            Integer pageC = Integer.parseInt(page);
            Integer sizeC = Integer.parseInt(size);
            Pageable pageable = PageRequest.of(pageC, sizeC);
            Page<ReceptionistDTO> ReceptionistDTOs = receptionistService.findAll(pageable);
            model.addAttribute("receptionistDTOs", ReceptionistDTOs);
            return "admin/manage-receptionists-for-admin";
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "receptionist not found");
            return "redirect:/admin/receptionist";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Unexpected Error");
            return "redirect:/admin/receptionist";
        }
    }

    @GetMapping(value = "/receptionist/{id}")
    public String showReceptionistById(@PathVariable(value = "id") String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Integer idC = Integer.parseInt(id);
            ReceptionistDTO ReceptionistDTO = receptionistService.findById(idC);
            model.addAttribute("receptionistDTO", ReceptionistDTO);
            return "admin/receptionist-details";
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Receptionist not found");
            return "redirect:/admin/receptionist";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Unexpected Error");
            return "redirect:/admin/receptionist";
        }
    }

    @GetMapping(value = "/receptionist/edit/{id}")
    public String editReceptionistById(@PathVariable(value = "id") String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Integer idC = Integer.parseInt(id);
            ReceptionistDTO receptionistDTO = receptionistService.findById(idC);
            model.addAttribute("receptionistDTO", receptionistDTO);
            return "admin/update-receptionist";
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Receptionist not found");
            return "redirect:/admin/receptionist";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Unexpected Error");
            return "redirect:/admin/receptionist";
        }
    }

    @PostMapping(value = "/receptionist/edit-result")
    public String editReceptionistResult(@Valid @ModelAttribute(name = "receptionistDTO") ReceptionistDTO dto,
                                         BindingResult bindingResult,
                                         @RequestParam("avatar") MultipartFile avatar,
                                         RedirectAttributes redirectAttributes,
                                         Model model) {
        if (userService.isMailNoDuplicate(dto.getEmail(), dto.getUserId())) {
            bindingResult.rejectValue("email", "error.email", "Email already exists");
        }
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
    public String deleteReceptionistById(@PathVariable(value = "id") String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Integer idC = Integer.parseInt(id);
            ReceptionistDTO receptionistDTO = receptionistService.findById(idC);
            model.addAttribute("receptionistDTO", receptionistDTO);
            return "admin/delete-receptionist";
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Receptionist not found");
            return "redirect:/admin/receptionist";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Unexpected Error");
            return "redirect:/admin/receptionist";
        }
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
        if (userService.isMailNoDuplicate(dto.getEmail(), dto.getUserId())) {
            bindingResult.rejectValue("email", "error.email", "Email already exists");
        }
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

    // Cashier

    @GetMapping(value = "/cashier")
    public String showCashierList(Model model,
                                  @RequestParam(value = "size", defaultValue = "10") Integer size,
                                  @RequestParam(value = "page", defaultValue = "0") Integer page) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CashierDTO> cashierDTOs = CashierService.findAll(pageable);
        model.addAttribute("cashierDTOs", cashierDTOs);
        return "admin/manage-cashiers-for-admin";
    }

    @GetMapping(value = "/cashier/{id}")
    public String showCashierById(@PathVariable(value = "id") Integer id, Model model) {
        CashierDTO cashierDTO = CashierService.findById(id);
        model.addAttribute("cashierDTO", cashierDTO);
        return "admin/cashier-details";
    }

    @GetMapping(value = "/cashier/edit/{id}")
    public String editCashierById(@PathVariable(value = "id") Integer id, Model model) {
        CashierDTO cashierDTO = CashierService.findById(id);
        model.addAttribute("cashierDTO", cashierDTO);
        return "admin/update-cashier";
    }

    @PostMapping(value = "/cashier/edit-result")
    public String editCashierResult(@Valid @ModelAttribute(name = "cashierDTO") CashierDTO dto,
                                    BindingResult bindingResult,
                                    @RequestParam("avatar") MultipartFile avatar,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("cashierDTO", dto);
            return "admin/update-cashier";
        }
        try {
            CashierService.update(dto, avatar);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Cashier with ID: " + dto.getUserId() + " was updated successfully!");
            return "redirect:/admin/cashier";
        } catch (Exception e) {
            model.addAttribute("cashierDTO", dto);
            return "admin/update-cashier";
        }
    }


    @GetMapping(value = "/cashier/delete/{id}")
    public String deleteCashierById(@PathVariable(value = "id") Integer id, Model model) {
        CashierDTO cashierDTO = CashierService.findById(id);
        model.addAttribute("cashierDTO", cashierDTO);
        return "admin/delete-cashier";
    }

    @PostMapping(value = "/cashier/delete-result")
    public String deleteCashierById(@ModelAttribute(name = "cashierDTO") CashierDTO dto, RedirectAttributes redirectAttributes) {
        try {
            CashierService.delete(dto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Cashier with ID: " + dto.getUserId() + " was deleted successfully!");
            return "redirect:/admin/cashier";
        } catch (Exception e) {
            System.out.println("\n" + "Error: " + e.getMessage() + "\n");
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Cannot delete the Cashier with ID: " + dto.getUserId());
            return "redirect:/admin/cashier";
        }
    }

    @GetMapping(value = "/cashier/new")
    public String addNewCashier(Model model) {
        model.addAttribute("cashierDTO", new CashierDTO());
        return "admin/add-new-cashier";
    }

    @PostMapping(value = "/cashier/new-result")
    public String addNewCashierResult(
            @Valid @ModelAttribute("cashierDTO") CashierDTO dto,
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
            return "admin/add-new-cashier";
        } else {
            try {
                Integer CashierId = CashierService.newCashier(dto, avatar);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Cashier with ID: " + CashierId + " was created successfully!");
                return "redirect:/admin/cashier";
            } catch (Exception e) {
                System.out.println(e.getMessage() + "Error ------------------------------\n");
                return "admin/add-new-cashier";
            }

        }

    }

    // Technician

    @GetMapping(value = "/technician")
    public String showTechnicianList(Model model,
                                     @RequestParam(value = "size", defaultValue = "10") Integer size,
                                     @RequestParam(value = "page", defaultValue = "0") Integer page) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TechnicianDTO> technicianDTOs = TechnicianService.findAll(pageable);
        model.addAttribute("technicianDTOs", technicianDTOs);
        return "admin/manage-technicians-for-admin";
    }

    @GetMapping(value = "/technician/{id}")
    public String showTechnicianById(@PathVariable(value = "id") Integer id, Model model) {
        TechnicianDTO technicianDTO = TechnicianService.findById(id);
        model.addAttribute("technicianDTO", technicianDTO);
        return "admin/technician-details";
    }

    @GetMapping(value = "/technician/edit/{id}")
    public String editTechnicianById(@PathVariable(value = "id") Integer id, Model model) {
        TechnicianDTO technicianDTO = TechnicianService.findById(id);
        model.addAttribute("technicianDTO", technicianDTO);
        return "admin/update-technician";
    }

    @PostMapping(value = "/technician/edit-result")
    public String editTechnicianResult(@Valid @ModelAttribute(name = "technicianDTO") TechnicianDTO dto,
                                       BindingResult bindingResult,
                                       @RequestParam("avatar") MultipartFile avatar,
                                       RedirectAttributes redirectAttributes,
                                       Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("technicianDTO", dto);
            return "admin/update-technician";
        }
        try {
            TechnicianService.update(dto, avatar);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Technician with ID: " + dto.getUserId() + " was updated successfully!");
            return "redirect:/admin/technician";
        } catch (Exception e) {
            model.addAttribute("technicianDTO", dto);
            return "admin/update-technician";
        }
    }


    @GetMapping(value = "/technician/delete/{id}")
    public String deleteTechnicianById(@PathVariable(value = "id") Integer id, Model model) {
        TechnicianDTO technicianDTO = TechnicianService.findById(id);
        model.addAttribute("technicianDTO", technicianDTO);
        return "admin/delete-technician";
    }

    @PostMapping(value = "/technician/delete-result")
    public String deleteTechnicianById(@ModelAttribute(name = "technicianDTO") TechnicianDTO dto, RedirectAttributes redirectAttributes) {
        try {
            TechnicianService.delete(dto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Technician with ID: " + dto.getUserId() + " was deleted successfully!");
            return "redirect:/admin/technician";
        } catch (Exception e) {
            System.out.println("\n" + "Error: " + e.getMessage() + "\n");
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Cannot delete the Technician with ID: " + dto.getUserId());
            return "redirect:/admin/technician";
        }
    }

    @GetMapping(value = "/technician/new")
    public String addNewTechnician(Model model) {
        model.addAttribute("technicianDTO", new TechnicianDTO());
        return "admin/add-new-technician";
    }

    @PostMapping(value = "/technician/new-result")
    public String addNewTechnicianResult(
            @Valid @ModelAttribute("technicianDTO") TechnicianDTO dto,
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
            return "admin/add-new-technician";
        } else {
            try {
                Integer TechnicianId = TechnicianService.newTechnician(dto, avatar);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Technician with ID: " + TechnicianId + " was created successfully!");
                return "redirect:/admin/technician";
            } catch (Exception e) {
                System.out.println(e.getMessage() + "Error ------------------------------\n");
                return "admin/add-new-technician";
            }

        }
    }

    // Dashboard
    @GetMapping(value = "/dashboard")
    public String showAdminDashboard(Model model,
                                     @RequestParam(defaultValue = "month") String filter,
                                     @RequestParam(required = false) Integer month,
                                     @RequestParam(required = false) Integer year) {

        List<Feedback> feedbacks = feedbackService.getFeedbackByFilter(filter);
        long totalPatients = patientService.getTotalPatientByFilter(filter);
        Double revenue = billService.getRevenue(filter,month,year);
        Double avgRating = feedbackService.getAvgRatingByFilter(filter);

        model.addAttribute("filter", filter);
        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("chartLabels", billService.getLabels(filter));
        model.addAttribute("chartRevenue", billService.getRevenueData(filter));
        model.addAttribute("totalPatients", totalPatients);
        model.addAttribute("revenue", revenue);
        model.addAttribute("avgRating", avgRating);

        System.out.println( billService.getLabels(filter));
        System.out.println( billService.getRevenueData(filter));

        return "admin/admin-dashboard";
    }


}
