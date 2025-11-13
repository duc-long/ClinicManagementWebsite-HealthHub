package com.group4.clinicmanagement.controller.doctor;

import com.group4.clinicmanagement.dto.*;
import com.group4.clinicmanagement.dto.doctor.*;
import com.group4.clinicmanagement.dto.doctor.LabRequestDTO;
import com.group4.clinicmanagement.dto.doctor.LabResultDTO;
import com.group4.clinicmanagement.dto.doctor.PrescriptionDetailDTO;
import com.group4.clinicmanagement.entity.*;
import com.group4.clinicmanagement.enums.AppointmentStatus;
import com.group4.clinicmanagement.repository.AppointmentRepository;
import com.group4.clinicmanagement.repository.DepartmentRepository;
import com.group4.clinicmanagement.repository.DoctorDailySlotRepository;
import com.group4.clinicmanagement.service.*;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/doctor")
public class DoctorController {
    private final DoctorService doctorService;
    private final UserService userService;
    private final DepartmentRepository departmentRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentService appointmentService;
    private final DrugCatalogService drugCatalogService;
    private final MedicalRecordService medicalRecordService;
    private final PrescriptionService prescriptionService;
    private final PrescriptionDetailService prescriptionDetailService;
    private final VitalSignsService vitalSignsService;
    private final LabRequestService labRequestService;
    private final LabResultService labResultService;
    private final LabTestCatalogService labTestCatalogService;
    private final PatientService patientService;
    private final DoctorDailySlotRepository slotRepository;

    public DoctorController(DoctorService doctorService, UserService userService, DepartmentRepository departmentRepository,
                            AppointmentRepository appointmentRepository, AppointmentService appointmentService,
                            DrugCatalogService drugCatalogService, MedicalRecordService medicalRecordService,
                            PrescriptionService prescriptionService, VitalSignsService vitalSignsService,
                            PrescriptionDetailService prescriptionDetailService, PatientService patientService,
                            LabRequestService labRequestService, LabTestCatalogService labTestCatalogService,
                            LabResultService labResultService, DoctorDailySlotRepository slotRepository) {
        this.doctorService = doctorService;
        this.userService = userService;
        this.departmentRepository = departmentRepository;
        this.appointmentRepository = appointmentRepository;
        this.appointmentService = appointmentService;
        this.drugCatalogService = drugCatalogService;
        this.medicalRecordService = medicalRecordService;
        this.vitalSignsService = vitalSignsService;
        this.prescriptionService = prescriptionService;
        this.labRequestService = labRequestService;
        this.patientService = patientService;
        this.prescriptionDetailService = prescriptionDetailService;
        this.labTestCatalogService = labTestCatalogService;
        this.labResultService = labResultService;
        this.slotRepository = slotRepository;
    }

    // method to load home view doctor
    @GetMapping("/home")
    public String home(Model model,
                       Principal principal,
                       @RequestParam(value = "patientName", required = false) String patientName,
                       @RequestParam(value = "status", required = false) AppointmentStatus status) {
        Staff user = userService.findUserByUsername(principal.getName());

        // check user null
        if (user == null) {
            return "redirect:/doctor/login";
        }

        List<AppointmentDTO> appointments = appointmentService.getTodayAppointments(user.getStaffId(), patientName);
        model.addAttribute("appointments", appointments);
        model.addAttribute("patientName", patientName);
        model.addAttribute("status", status);
        model.addAttribute("section", "home");
        model.addAttribute("active", "home");

        return "doctor/home";
    }

    // method to load appointment list for doctor (delete)
    @GetMapping("/appointments")
    public String loadAppointment(Principal principal, Model model, RedirectAttributes redirectAttributes) {
        Staff user = userService.findUserByUsername(principal.getName());
        if (user == null) return "redirect:/doctor/login";

        Doctor doctor = doctorService.findDoctorById(user.getStaffId());
        model.addAttribute("doctor", doctor);

        try {
            List<AppointmentDTO> appointmentList = appointmentRepository
                    .findByDoctor_DoctorIdAndStatusValue(user.getStaffId(), AppointmentStatus.CHECKED_IN.getValue())
                    .stream()
                    .map(a -> new AppointmentDTO(
                            a.getAppointmentId(),
                            a.getPatient().getPatientId(),
                            a.getDoctor() != null && a.getDoctor().getStaff() != null ? a.getDoctor().getStaff().getFullName() : "Unknown",
                            a.getPatient() != null && a.getPatient() != null ? a.getPatient().getFullName() : "Unknown",
                            a.getReceptionist() != null ? a.getReceptionist().getFullName() : "Unknown",
                            a.getAppointmentDate(),
                            a.getCreatedAt(),
                            a.getStatus(),
                            a.getQueueNumber(),
                            a.getNotes(),
                            a.getCancelReason()
                    ))
                    .toList();
            model.addAttribute("appointments", appointmentList);
            model.addAttribute("section", "appointments");
            return "doctor/home";
        } catch (Exception e) {
            System.out.println("Lỗi excepetion");
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/doctor/home";
        }
    }

    // method to show appointment detail for doctor
    @GetMapping("/appointments/detail/{id}")
    public String loadAppointmentDetail(@PathVariable Integer id, Model model,
                                        RedirectAttributes redirectAttributes,
                                        Principal principal) {
        try {
            if (id == null) {
                redirectAttributes.addFlashAttribute("messageType", "error");
                redirectAttributes.addFlashAttribute("message", "Invalid Appointment ID");
                return "redirect:/doctor/home";
            }
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/doctor/home";
        }


        Appointment a = appointmentService.getById(id);
        if (a == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Invalid Appointment ID");
            return "redirect:/doctor/home";
        }

        Staff user = userService.findUserByUsername(principal.getName());

        if (user.getStaffId() != a.getDoctor().getDoctorId()) {
            redirectAttributes.addFlashAttribute("message", "You can't access this appointment");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/doctor/home";
        }

        MedicalRecord medicalRecord = medicalRecordService.findByAppointmentId(id);
        Integer medicalRecordId = (medicalRecord != null) ? medicalRecord.getRecordId() : null;

        AppointmentDTO dto = new AppointmentDTO(
                a.getAppointmentId(),
                a.getPatient().getPatientId(),
                a.getDoctor().getStaff().getFullName(),
                a.getPatient().getFullName(),
                a.getReceptionist() != null ? a.getReceptionist().getFullName() : "N/A",
                a.getAppointmentDate(),
                a.getCreatedAt(),
                a.getStatus(),
                a.getQueueNumber() != null ? a.getQueueNumber() : 0,
                a.getNotes(),
                a.getCancelReason()
        );

        model.addAttribute("appointment", dto);
        model.addAttribute("section", "appointment-detail");
        model.addAttribute("active", "home");
        model.addAttribute("recordId", medicalRecordId);
        return "doctor/home";
    }

    // method to load profile doctor
    @GetMapping("/profile")
    public String loadProfile(Principal principal, Model model) {
        Staff user = userService.findUserByUsername(principal.getName());
        if (user == null) return "redirect:/doctor/login";

        Doctor doctor = doctorService.findDoctorById(user.getStaffId());
        model.addAttribute("doctor", doctor);

        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setGender(user.getGender());
        doctorDTO.setEmail(user.getEmail());
        doctorDTO.setPhone(user.getPhone());
        doctorDTO.setFullName(user.getFullName());
        doctorDTO.setDoctorId(user.getStaffId());
        doctorDTO.setAvatarFileName(user.getAvatar());
        doctorDTO.setUsername(user.getUsername());
        doctorDTO.setDepartmentId(doctor.getDepartment().getDepartmentId());
        Department department = departmentRepository.findByDepartmentId(doctorDTO.getDepartmentId())
                .orElse(null);


        model.addAttribute("doctor", doctorDTO);
        model.addAttribute("department", department.getName());
        model.addAttribute("section", "profile");
        model.addAttribute("active", "profile");
        return "doctor/home";
    }

    // method to redirect to update profile page
    @GetMapping("/profile/edit")
    public String loadProfile(Model model, Principal principal) {
        Staff user = userService.findUserByUsername(principal.getName());

        Doctor doctor = doctorService.findDoctorById(user.getStaffId());
        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setGender(user.getGender());
        doctorDTO.setEmail(user.getEmail());
        doctorDTO.setPhone(user.getPhone());
        doctorDTO.setFullName(user.getFullName());
        doctorDTO.setDoctorId(user.getStaffId());
        doctorDTO.setAvatarFileName(user.getAvatar());
        doctorDTO.setUsername(user.getUsername());
        doctorDTO.setBio(doctor.getBio());

        model.addAttribute("doctor", doctorDTO);
        return "doctor/edit-profile";
    }

    // method to update doctor profile
    @PostMapping("/profile/update")
    public String doUpdateProfile(Model model,
                                  Principal principal,
                                  @ModelAttribute("doctor") DoctorDTO doctorModel,
                                  RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "You must be logged in to perform this action.");
            return "redirect:/doctor/login";
        }

        Staff user = userService.findUserByUsername(principal.getName());
        if (user == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "User not found.");
            return "redirect:/doctor/login";
        }

        try {
            Optional<String> validationError = doctorService.validateUpdateDoctorInfo(doctorModel, user.getStaffId());
            if (validationError.isPresent()) {
                redirectAttributes.addFlashAttribute("messageType", "error");
                redirectAttributes.addFlashAttribute("message", validationError.get());
                return "redirect:/doctor/profile/edit";
            }

            // Update User
            user.setFullName(doctorModel.getFullName().trim());
            user.setEmail(doctorModel.getEmail().trim());
            user.setPhone(doctorModel.getPhone().trim());
            user.setGender(doctorModel.getGender());
            Staff saved = userService.saveUser(user);

            if (saved == null) {
                redirectAttributes.addFlashAttribute("messageType", "error");
                redirectAttributes.addFlashAttribute("message", "Failed to update user account.");
                return "redirect:/doctor/profile/edit";
            }

            // Update Doctor-specific
            Doctor doctor = doctorService.findDoctorById(user.getStaffId());
            if (doctor == null) {
                // create new doctor record if not exist (optional)
                doctor = new Doctor();
                doctor.setDoctorId(user.getStaffId());
            }
            doctor.setBio(doctorModel.getBio() == null ? null : doctorModel.getBio().trim());
            doctorService.saveDoctor(doctor);

            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Doctor profile updated successfully!");
            return "redirect:/doctor/profile";
        } catch (Exception ex) {
            // logger.error("Error updating doctor profile", ex);
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "An unexpected error occurred while updating profile. Please try again later.");
            return "redirect:/doctor/profile/edit";
        }
    }

    // method to change doctor avatar
    @PostMapping("/change-avatar")
    public String editAvatar(
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            String doctorName = authentication.getName();
            doctorService.updateDoctorProfile(doctorName, avatarFile);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            return "redirect:/doctor/profile";

        } catch (IllegalArgumentException e) {
            // lỗi do file (validate) -> trả về trang edit và show message
            model.addAttribute("fileError", e.getMessage());
            return "doctor/edit-profile";
        } catch (Exception e) {
            // lỗi bất ngờ -> redirect và show error
            redirectAttributes.addFlashAttribute("errorMessage", "Unexpected error while saving profile.");
            return "redirect:/doctor/profile";
        }
    }

    // method to show change password
    @GetMapping("/change-password")
    public String changePassword(Model model, Principal principal) {
        Staff user = userService.findUserByUsername(principal.getName());

        if (user == null) {
            return "redirect:/doctor/home";
        }

        model.addAttribute("active", "profile");
        model.addAttribute("section", "change-password");
        return "doctor/home";
    }

    // method to change doctor password
    @PostMapping("/change-password")
    public String changePassword(RedirectAttributes redirectAttributes, Principal principal,
                                 @RequestParam(name = "currentPassword") String currentPassword,
                                 @RequestParam(name = "newPassword") String newPassword,
                                 @RequestParam(name = "confirmPassword", required = false) String confirmPassword) {
        Staff user = userService.findUserByUsername(principal.getName());
        if (user == null) {
            return "redirect:/doctor/home";
        }

        // basic server-side checks
        if (confirmPassword == null || !newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "New password and confirm password do not match!");
            return "redirect:/doctor/change-password";
        }

        boolean changed = userService.changePassword(user, currentPassword, newPassword);
        if (changed) {
            // clean login information
            SecurityContextHolder.clearContext();
            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Password changed successfully. Please log in again.");
            return "redirect:/doctor/login";
        } else {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Change password failed. Check your current password or password policy.");
            return "redirect:/doctor/change-password";
        }
    }

    // method to show crate medical record
    @PostMapping("/records/show-create")
    public String createMedicalRecord(@RequestParam("appointmentId") Integer appointmentId,
                                      @RequestParam("patientId") Integer patientId,
                                      Model model) {
        model.addAttribute("appointmentId", appointmentId);
        model.addAttribute("patientId", patientId);
        model.addAttribute("section", "create-record");
        model.addAttribute("active", "home");
        model.addAttribute("record", new MedicalRecordDTO());
        return "doctor/home";
    }

    // method to show create record form
    @GetMapping("/records/{id}")
    public String loadRecordForm(Model model, Principal principal,
                                 @RequestParam(name = "patientId") int patientId,
                                 @RequestParam(name = "appointmentId") int appointmentId) {
        Staff user = userService.findUserByUsername(principal.getName());
        if (user == null) return "redirect:/doctor/login";

        model.addAttribute("record", new MedicalRecordDTO());
        model.addAttribute("appointmentId", appointmentId);
        model.addAttribute("patientId", patientId);
        model.addAttribute("active", "home");
        model.addAttribute("section", "create-record");
        return "doctor/home";
    }

    // method to show update record form
    @GetMapping("/records/update/{id}")
    public String updateRecord(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        if (id == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Invalid record id");
            return "redirect:/doctor/home";
        }

        MedicalRecordDTO record = medicalRecordService.findDTOById(id);

        // check record null
        if (record == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Record not found!");
            return "redirect:/doctor/home";
        }

        model.addAttribute("record", record);
        model.addAttribute("mode", "update");
        return "doctor/medical-record";
    }

    // method to do update record
    @PostMapping("/records/update")
    public String updateRecord(Model model, Principal principal,
                               @ModelAttribute("record") MedicalRecordDTO record,
                               RedirectAttributes redirectAttributes) {

        Staff user = userService.findUserByUsername(principal.getName());
        if (user == null) {
            return "redirect:/doctor/login";
        }

        try {
            MedicalRecord medicalRecord = medicalRecordService.findById(record.getRecordId());
            if (medicalRecord == null) {
                redirectAttributes.addFlashAttribute("messageType", "error");
                redirectAttributes.addFlashAttribute("message", "Medical record not found!");
                return "redirect:/doctor/home";
            }

            // update new information
            medicalRecord.setDiagnosis(record.getDiagnosis());
            medicalRecord.setNotes(record.getNotes());
            medicalRecord.setStatus(record.getRecordStatus());

            int updated = medicalRecordService.updateRecord(medicalRecord);

            redirectAttributes.addFlashAttribute("messageType", updated > 0 ? "success" : "error");
            redirectAttributes.addFlashAttribute("message", updated > 0 ?
                    "Updated medical record successfully!" : "Failed to update medical record!");

            return "redirect:/doctor/records/detail/" + record.getRecordId();

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "An unexpected error occurred!");
            return "redirect:/doctor/home";
        }
    }

    // method to show medical record detail
    @GetMapping("/records/detail/{recordId}")
    public String viewRecordDetail(@PathVariable Integer recordId, Model model, RedirectAttributes redirectAttributes) {
        // check valid record ID
        if (recordId == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Record ID not found!");
            return "redirect:/doctor/home";
        }

        // switch to  MedicalRecordDTO
        MedicalRecordDTO recordDTO = medicalRecordService.findDTOById(recordId);
        if (recordDTO == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Record ID not found!");
            return "redirect:/doctor/home";
        }

        // lab request Id
        LabRequest labRequest = labRequestService.isExistLabRequest(recordId);
        model.addAttribute("labRequest", labRequest);
        // VitalSigns DTO
        VitalSignsDTO vitalSignsDTO = vitalSignsService.findVitalSignsDTOById(recordId);

        // Prescription DTO
        PrescriptionDTO prescriptionDTO = prescriptionService.getPrescriptionDTOByRecordId(recordDTO.getRecordId());
        if (prescriptionDTO != null) {
            prescriptionDTO.setRecordId(recordDTO.getRecordId());

            // map to PrescriptionDetailDTO
            List<PrescriptionDetailDTO> details = prescriptionDetailService.getDetailsDTOByPrescriptionID(prescriptionDTO.getPrescriptionId());

            if (details == null) {
                redirectAttributes.addFlashAttribute("messageType", "error");
                redirectAttributes.addFlashAttribute("message", "Prescription details not found!");
                return "redirect:/doctor/home";
            }

            // set prescription detail
            prescriptionDTO.setPrescriptionDetails(details);
        }


        model.addAttribute("record", recordDTO);
        model.addAttribute("vitalSigns", vitalSignsDTO);
        model.addAttribute("prescription", prescriptionDTO);
        model.addAttribute("section", "record-detail");
        model.addAttribute("active", "home");
        model.addAttribute("appointmentId", recordDTO.getAppointmentId());
        return "doctor/home";
    }

    // method to do create medical record
    @PostMapping("/records/create")
    public String createRecord(Principal principal,
                               @ModelAttribute("record") MedicalRecordDTO record,
                               RedirectAttributes redirectAttributes) {

        Staff user = userService.findUserByUsername(principal.getName());
        if (user == null) {
            return "redirect:/doctor/login";
        }

        try {
            Doctor doctor = doctorService.findDoctorById(user.getStaffId());
            Patient patient = patientService.findPatientById(record.getPatientId());
            Appointment appointment = appointmentService.findById(record.getAppointmentId());

            if (patient == null || appointment == null) {
                redirectAttributes.addFlashAttribute("messageType", "error");
                redirectAttributes.addFlashAttribute("message", "Invalid patient or appointment!");
                return "redirect:/doctor/home";
            }

            MedicalRecord medicalRecord = new MedicalRecord();
            medicalRecord.setDoctor(doctor);
            medicalRecord.setPatient(patient);
            medicalRecord.setAppointment(appointment);
            medicalRecord.setDiagnosis(record.getDiagnosis());
            medicalRecord.setNotes(record.getNotes());
            medicalRecord.setStatus(record.getRecordStatus());
            medicalRecord.setCreatedBy(user);

            MedicalRecord saveRecord = medicalRecordService.saveRecord(medicalRecord);
            if (saveRecord == null) {
                redirectAttributes.addFlashAttribute("messageType", "error");
                redirectAttributes.addFlashAttribute("message", "Failed to save medical record!");
                return "redirect:/doctor/home";
            }

            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Record created successfully!");
            return "redirect:/doctor/records/detail/" + saveRecord.getRecordId();
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "An unexpected error occurred!");
            return "redirect:/doctor/home";
        }
    }

    // method to show create prescription form
    @GetMapping("/prescription/create/{id}")
    public String showCreateForm(@PathVariable("id") int recordId,
                                 Model model, Principal principal, RedirectAttributes redirectAttributes) {
        Staff user = userService.findUserByUsername(principal.getName());
        if (user == null) return "redirect:/doctor/login";

        MedicalRecord record = medicalRecordService.findById(recordId);
        if (record == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Medical record not found!");
            return "redirect:/doctor/home";
        }

        // drug DTO
        List<DrugCatalogDTO> drugCatalogDTOS = drugCatalogService.findAllDrugs()
                .stream()
                .map(drug -> {
                    DrugCatalogDTO drugDTO = new DrugCatalogDTO();
                    drugDTO.setDrugName(drug.getName());
                    drugDTO.setDrugId(drug.getDrugId());
                    return drugDTO;
                }).toList();

        model.addAttribute("record", record);
        model.addAttribute("doctorId", user.getDoctor().getDoctorId());
        model.addAttribute("drugs", drugCatalogDTOS);
        return "doctor/create-prescription";
    }

    // method to show prescription for update
    @GetMapping("/prescription/update/{id}")
    public String showUpdatePrescriptionForm(@PathVariable("id") Integer prescriptionId,
                                             Model model,
                                             Principal principal,
                                             RedirectAttributes redirectAttributes) {

        if (prescriptionId == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Prescription ID is required!");
            return "redirect:/doctor/home";
        }

        Staff user = userService.findUserByUsername(principal.getName());
        if (user == null) return "redirect:/doctor/login";

        Prescription prescription = prescriptionService.findPrescriptionById(prescriptionId);
        if (prescription == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Prescription not found!");
            return "redirect:/doctor/home";
        }

        MedicalRecord medicalRecord = medicalRecordService.findById(prescription.getMedicalRecord().getRecordId());
        MedicalRecordDTO recordDTO = new MedicalRecordDTO();
        recordDTO.setRecordId(medicalRecord.getRecordId());
        recordDTO.setDoctorId(medicalRecord.getDoctor().getDoctorId());
        recordDTO.setRecordStatus(medicalRecord.getStatus());
        recordDTO.setDiagnosis(medicalRecord.getDiagnosis());
        recordDTO.setNotes(medicalRecord.getNotes());
        recordDTO.setDoctorName(medicalRecord.getDoctor().getStaff().getFullName());
        recordDTO.setPatientName(medicalRecord.getPatient().getFullName());

        List<PrescriptionDetailDTO> detailDTOs = prescription.getDetails().stream()
                .map(detail -> {
                    PrescriptionDetailDTO dto = new PrescriptionDetailDTO();
                    dto.setPrescriptionDetailId(detail.getDetailId());
                    dto.setPrescriptionId(prescriptionId);
                    dto.setDrugId(detail.getDrug().getDrugId());
                    dto.setDrugName(detail.getDrug().getName());
                    dto.setDosage(detail.getDosage());
                    dto.setFrequency(detail.getFrequency());
                    dto.setDuration(detail.getDurationDays());
                    dto.setQuantity(detail.getQuantity());
                    dto.setInstruction(detail.getInstruction());
                    return dto;
                })
                .toList();

        List<DrugCatalogDTO> drugs = drugCatalogService.findAllDrugs().stream()
                .map(drug -> {
                    DrugCatalogDTO dto = new DrugCatalogDTO();
                    dto.setDrugId(drug.getDrugId());
                    dto.setDrugName(drug.getName());
                    dto.setDescription(drug.getDescription());
                    return dto;
                })
                .toList();

        model.addAttribute("mode", "update");
        model.addAttribute("record", recordDTO);
        model.addAttribute("prescription", prescription);
        model.addAttribute("details", detailDTOs);
        model.addAttribute("drugs", drugs);
        model.addAttribute("recordId", medicalRecord.getRecordId());
        return "doctor/prescription";
    }

    // method to do create and update prescription form
    @PostMapping("/prescription/save-details")
    public String savePrescriptionDetails(
            @RequestParam("recordId") int recordId,
            @RequestParam("prescriptionId") int prescriptionId,
            @RequestParam(value = "detailIds", required = false) List<Integer> detailIds,
            @RequestParam(value = "drugIds", required = false) List<Integer> drugIds,
            @RequestParam(value = "quantities", required = false) List<Integer> quantities,
            @RequestParam(value = "dosages", required = false) List<String> dosages,
            @RequestParam(value = "frequencies", required = false) List<String> frequencies,
            @RequestParam(value = "durationDay", required = false) List<Integer> durationDays,
            @RequestParam(value = "instructions", required = false) List<String> instructions,
            RedirectAttributes redirectAttributes
    ) {
        try {
            detailIds = (detailIds == null) ? new ArrayList<>() : detailIds;
            drugIds = (drugIds == null) ? new ArrayList<>() : drugIds;
            quantities = (quantities == null) ? new ArrayList<>() : quantities;
            dosages = (dosages == null) ? new ArrayList<>() : dosages;
            frequencies = (frequencies == null) ? new ArrayList<>() : frequencies;
            durationDays = (durationDays == null) ? new ArrayList<>() : durationDays;
            instructions = (instructions == null) ? new ArrayList<>() : instructions;

            int rows = drugIds.size();
            if (rows == 0) {
                throw new IllegalArgumentException("Please add at least one drug.");
            }

            if (!(quantities.size() == rows && dosages.size() == rows
                    && frequencies.size() == rows && durationDays.size() == rows
                    && instructions.size() == rows)) {
                throw new IllegalArgumentException("Mismatch in prescription rows. Please check all fields.");
            }

            for (int i = 0; i < rows; i++) {
                Integer drugId = drugIds.get(i);
                Integer qty = quantities.get(i);
                String dosage = dosages.get(i);
                String freq = frequencies.get(i);
                Integer dur = durationDays.get(i);

                if (drugId == null || drugId <= 0) {
                    throw new IllegalArgumentException("Invalid drug selected at row " + (i + 1));
                }
                if (qty == null || qty <= 0) {
                    throw new IllegalArgumentException("Quantity must be a positive number at row " + (i + 1));
                }
                if (dur == null || dur <= 0) {
                    throw new IllegalArgumentException("Duration must be a positive number at row " + (i + 1));
                }
                if (dosage == null || dosage.trim().isEmpty()) {
                    throw new IllegalArgumentException("Dosage cannot be empty at row " + (i + 1));
                }
                if (freq == null || freq.trim().isEmpty()) {
                    throw new IllegalArgumentException("Frequency cannot be empty at row " + (i + 1));
                }
            }

            // Nếu pass hết validation, gọi service
            prescriptionDetailService.saveOrUpdate(
                    prescriptionId, detailIds, drugIds, quantities,
                    dosages, frequencies, durationDays, instructions
            );

            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Prescription details saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Error: " + e.getMessage());
        }
        return "redirect:/doctor/records/detail/" + recordId;
    }

    // method to do create and update prescription form
    @PostMapping("/prescription/create-prescription")
    public String createPrescriptionDetails(
            @RequestParam("recordId") int recordId,
            @RequestParam("doctorId") int doctorId,
            @RequestParam(value = "drugIds", required = false) List<Integer> drugIds,
            @RequestParam(value = "quantities", required = false) List<Integer> quantities,
            @RequestParam(value = "dosages", required = false) List<String> dosages,
            @RequestParam(value = "frequencies", required = false) List<String> frequencies,
            @RequestParam(value = "durationDay", required = false) List<Integer> durationDays,
            @RequestParam(value = "instructions", required = false) List<String> instructions,
            RedirectAttributes redirectAttributes
    ) {
        try {
            if (drugIds != null && !drugIds.isEmpty()) {
                quantities = (quantities == null) ? new ArrayList<>() : quantities;
                dosages = (dosages == null) ? new ArrayList<>() : dosages;
                frequencies = (frequencies == null) ? new ArrayList<>() : frequencies;
                durationDays = (durationDays == null) ? new ArrayList<>() : durationDays;
                instructions = (instructions == null) ? new ArrayList<>() : instructions;

                int rows = drugIds.size();

                if (!(quantities.size() == rows && dosages.size() == rows
                        && frequencies.size() == rows && durationDays.size() == rows
                        && instructions.size() == rows)) {
                    throw new IllegalArgumentException("Mismatch in prescription fields: please fill all columns for every drug.");
                }

                for (int i = 0; i < rows; i++) {
                    Integer drugId = drugIds.get(i);
                    Integer qty = quantities.get(i);
                    Integer dur = durationDays.get(i);
                    String dosage = dosages.get(i);
                    String freq = frequencies.get(i);

                    if (drugId == null || drugId <= 0) {
                        throw new IllegalArgumentException("Invalid drug selected at row " + (i + 1));
                    }
                    if (qty == null || qty <= 0) {
                        throw new IllegalArgumentException("Quantity must be a positive number at row " + (i + 1));
                    }
                    if (dur == null || dur <= 0) {
                        throw new IllegalArgumentException("Duration must be a positive number at row " + (i + 1));
                    }
                    if (dosage == null || dosage.trim().isEmpty()) {
                        throw new IllegalArgumentException("Dosage cannot be empty at row " + (i + 1));
                    }
                    if (freq == null || freq.trim().isEmpty()) {
                        throw new IllegalArgumentException("Frequency cannot be empty at row " + (i + 1));
                    }
                }

                Prescription saved = prescriptionService.createPrescription(recordId, doctorId);
                prescriptionDetailService.addDetails(saved.getPrescriptionId(),
                        drugIds, quantities, dosages, frequencies, durationDays, instructions);

            } else {
                prescriptionService.createPrescription(recordId, doctorId);
            }

            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Prescription created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Error: " + e.getMessage());
        }
        return "redirect:/doctor/records/detail/" + recordId;
    }


    // method to show vital create
    @GetMapping("/vitals/create/{id}")
    public String showVitalForm(@PathVariable("id") Integer recordId,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        if (recordId == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Record not found!");
            return "redirect:/doctor/home";
        }

        VitalSignsDTO dto = new VitalSignsDTO();
        dto.setRecordId(recordId);
        model.addAttribute("vital", dto);
        model.addAttribute("mode", "create");

        model.addAttribute("recordId", recordId);
        return "doctor/vitalSign";
    }

    // method to show vital sign form for update
    @GetMapping("/vitals/update/{id}")
    public String vitalSignUpdatePage(Model model, @PathVariable(name = "id") Integer vitalSignId,
                                      RedirectAttributes redirectAttributes) {
        if (vitalSignId == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Record not found!");
            return "redirect:/doctor/home";
        }

        VitalSignsDTO vitalSignsDTO = vitalSignsService.findVitalSignsDTOById(vitalSignId);

        if (vitalSignsDTO == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "VitalSigns not found!");
            return "redirect:/doctor/home";
        }

        model.addAttribute("recordId", vitalSignsDTO.getRecordId());
        model.addAttribute("vital", vitalSignsDTO);
        return "doctor/vitalSign";
    }

    // method do save update vital sign
    @PostMapping("/vitals/update")
    public String saveVital(@ModelAttribute("vital") VitalSignsDTO vital,
                            BindingResult result, Model model,
                            @RequestParam("recordId") int recordId,
                            Principal principal,
                            RedirectAttributes redirectAttributes) {
        Staff user = userService.findUserByUsername(principal.getName());
        if (user == null) {
            return "redirect:/doctor/home";
        }

        if (result.hasErrors()) {
            model.addAttribute("recordId", vital.getRecordId());
            model.addAttribute("mode", "update");
            model.addAttribute("recordId", recordId);
            return "doctor/vitalSign";
        }
        vitalSignsService.saveOrUpdate(recordId, vital, user.getDoctor());
        redirectAttributes.addFlashAttribute("messageType", "success");
        redirectAttributes.addFlashAttribute("message", "Vital signs saved successfully!");

        return "redirect:/doctor/records/detail/" + recordId;
    }

    // method to show lab request fragment
    @GetMapping("/labs/create/{id}")
    public String labCreatePage(Model model, @PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes,
                                Principal principal) {
        Staff user = userService.findUserByUsername(principal.getName());
        if (user == null) {
            return "redirect:/doctor/login";
        }
        // get all lab test catalog
        List<LabTestCatalogDTO> labCatalogs = labTestCatalogService.getAllLabTestDTO();

        // get medical record
        MedicalRecordDTO recordDTO = medicalRecordService.findDTOById(id);
        if (recordDTO == null) {
            return "redirect:/doctor/home";
        }

        // check valid lab test
        if (labCatalogs == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Lab Catalogs not found!");
            return "redirect:/doctor/home";
        }

        // check valid record ID
        if (id == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Medical Record does not exist!");
            return "redirect:/doctor/home";
        }

        // add to model
        model.addAttribute("record", recordDTO);
        model.addAttribute("labCatalogs", labCatalogs);
        model.addAttribute("doctorId", user.getStaffId());
        model.addAttribute("recordId", id);
        model.addAttribute("lab", new LabRequestDTO());
        model.addAttribute("active", "home");
        return "doctor/lab-request";
    }

    // method to do create lab request
    @PostMapping("/labs/create")
    public String doCreateLab(Model model, RedirectAttributes redirectAttributes,
                              @RequestParam(name = "recordId") Integer recordId,
                              @RequestParam(name = "doctorId") Integer doctorId,
                              @RequestParam(name = "testId") Integer testId) {
        if (recordId == null || doctorId == null || testId == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "ID not found!");
            return "redirect:/doctor/home";
        }

        LabRequest request = new LabRequest();
        // find doctor
        Doctor doctor = doctorService.findDoctorById(doctorId);
        // find lab test
        LabTestCatalog testCatalog = labTestCatalogService.findByTestId(testId);
        if (testCatalog == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Lab Tests not found!");
            return "redirect:/doctor/home";
        }

        // find medical record
        MedicalRecord medicalRecord = medicalRecordService.findById(recordId);
        // set object
        request.setDoctor(doctor);
        request.setTest(testCatalog);
        request.setMedicalRecord(medicalRecord);
        request.setRequestedAt(LocalDateTime.now());

        if (labRequestService.saveLabRequest(request) != null) {
            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Lab request saved successfully!");
            redirectAttributes.addFlashAttribute("active", "home");
            return "redirect:/doctor/records/detail/" + recordId;
        } else {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Lab request could not be saved!");
            return "redirect:/doctor/home";
        }
    }

    // method to view lab request
    @GetMapping("/labs/view/{id}")
    public String viewLabRequest(Model model, @PathVariable(name = "id") Integer labId,
                                 RedirectAttributes redirectAttributes) {
        if (labId == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Lab Request does not exist!");
            return "redirect:/doctor/home";
        }

        // get lab request DTO
        LabRequestDTO labRequestDTO = labRequestService.findLabRequestDTOById(labId);
        if (labRequestDTO == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Lab Request does not exist!");
            return "redirect:/doctor/home";
        }

        // get Lab test
        LabTestCatalogDTO testDTO = labTestCatalogService.getLabTestCatalogDTOByTestId(labRequestDTO.getLabTestCatalogId());
        if (testDTO == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Lab Test does not exist!");
            return "redirect:/doctor/home";
        }

        model.addAttribute("test", testDTO);
        model.addAttribute("lab", labRequestDTO);
        model.addAttribute("doctorId", labRequestDTO.getDoctorId());

        return "doctor/lab-request-detail";
    }

    @PostMapping("/labs/cancel/{id}")
    public String deleteLabRequest(@PathVariable("id") Integer labId,
                                   RedirectAttributes redirectAttributes) {
        if (labId == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Lab Request does not exist!");
            return "redirect:/doctor/home";
        }

        LabRequest labRequest = labRequestService.findLabRequestById(labId);

        if (labRequest == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Lab Request does not exist!");
            return "redirect:/doctor/home";
        }

        // get recordId
        int recordId = labRequest.getMedicalRecord().getRecordId();

        // delete lab request
        boolean isDeleteRequest = labRequestService.deleteRequestById(labId);

        // check delete
        if (!isDeleteRequest) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Failed to cancel Lab Request!");
        } else {
            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Lab request cancel successfully!");
        }

        return "redirect:/doctor/records/detail/" + recordId;
    }

    // method to edit lab request information
    @GetMapping("/labs/edit/{id}")
    public String editLabRequest(@PathVariable("id") Integer labId, Model model, RedirectAttributes redirectAttributes) {

        LabRequestDTO lab = labRequestService.findLabRequestDTOById(labId);
        if (lab == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Lab Request does not exist!");
            return "redirect:/doctor/home";
        }

        List<LabTestCatalogDTO> testDTO = labTestCatalogService.getAllLabTestDTO();
        if (testDTO == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Lab Test does not exist!");
            return "redirect:/doctor/home";
        }

        model.addAttribute("tests", testDTO);
        model.addAttribute("lab", lab);
        model.addAttribute("tests", testDTO);
        return "doctor/edit-lab-request";
    }

    // method to do edit lab request
    @PostMapping("/labs/edit")
    public String updateLabRequest(@ModelAttribute(name = "lab") LabRequestDTO dto, @RequestParam("testId") Integer testId,
                                   RedirectAttributes redirectAttributes) {
        LabRequest labRequest = labRequestService.findLabRequestById(dto.getLabRequestId());
        if (labRequest == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Lab Request does not exist!");
            return "redirect:/doctor/home";
        }

        LabTestCatalog test = labTestCatalogService.findByTestId(testId);
        if (test == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Lab Test does not exist!");
            return "redirect:/doctor/home";
        }

        labRequest.setTest(test);
        labRequest.setRequestedAt(LocalDateTime.now());

        LabRequest saveLabRequest = labRequestService.saveLabRequest(labRequest);
        if (saveLabRequest == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Failed to save Lab Request!");
        } else {
            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Lab Request updated successfully!");
        }

        return "redirect:/doctor/records/detail/" + labRequest.getMedicalRecord().getRecordId();
    }

    // method to get lab result view
    @GetMapping("/labs/view/result/{id}")
    public String viewLabResult(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        // check null for lab ID
        if (id == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Lab Request does not exist!");
            return "redirect:/doctor/home";
        }

        // get lab request DTO
        LabRequestDTO labRequest = labRequestService.findLabRequestDTOById(id);
        if (labRequest == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Lab Request does not exist!");
            return "redirect:/doctor/home";
        }

        // get lab result DTO
        LabResultDTO result = labResultService.getResultByLabRequestId(labRequest.getLabRequestId());
        if (result == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Lab Request does not exist!");
            return "redirect:/doctor/home";
        }

        // get lab test
        LabTestCatalogDTO test = labTestCatalogService.getLabTestCatalogDTOByTestId(labRequest.getLabTestCatalogId());
        if (test == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Lab Test does not exist!");
            return "redirect:/doctor/home";
        }

        // add to model
        model.addAttribute("test", test);
        model.addAttribute("lab", labRequest);
        model.addAttribute("result", result);
        model.addAttribute("images", result.getImages());

        return "doctor/lab-result-view";
    }

    // method to show create re-examination for patient
    @GetMapping("/re-examination/create")
    public String createExamination(Model model, Principal principal) {
        Staff user = userService.findUserByUsername(principal.getName());
        List<AppointmentDTO> appointmentDTOS = appointmentService.getTodayAppointmentsByDoctorId(user.getStaffId());

        model.addAttribute("appointments", appointmentDTOS);
        model.addAttribute("appointment", new Appointment());

        return "doctor/make-re-examination-appointment";
    }

    // method to do create appointment re-examination for patient
    @PostMapping("/make-appointment")
    public String doMakeAppointment(
            @ModelAttribute("appointment") Appointment appointment,
            @RequestParam(value = "patientId", required = false) Integer patientId,
            RedirectAttributes redirectAttributes,
            Principal principal) {

        if (patientId == null) {
            redirectAttributes.addFlashAttribute("message", "Please select an appointment / patient first.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/doctor/re-examination/create";
        }

        Patient patient = patientService.findPatientById(patientId);
        if (patient == null) {
            redirectAttributes.addFlashAttribute("message", "Selected patient not found.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/doctor/re-examination/create";
        }

        LocalDate apptDate = appointment.getAppointmentDate();
        if (apptDate == null) {
            redirectAttributes.addFlashAttribute("message", "Please choose an appointment date.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/doctor/re-examination/create";
        }

        if (!appointmentService.isBookAppointmentValidDate(apptDate)) {
            redirectAttributes.addFlashAttribute("message", "Invalid booking date. You must book at least 2 days in advance.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/doctor/re-examination/create";
        }

        // create new appointment
        Appointment newAppointment = new Appointment();
        newAppointment.setPatient(patient);
        newAppointment.setStatus(AppointmentStatus.CONFIRMED);
        newAppointment.setAppointmentDate(appointment.getAppointmentDate());
        newAppointment.setNotes(appointment.getNotes());


            Staff user = userService.findUserByUsername(principal.getName());
            if (user != null && user.getDoctor() != null) {
                newAppointment.setDoctor(user.getDoctor());
        }
        DoctorDailySlot slot = slotRepository.findByDoctorAndSlotDate(user.getDoctor(), appointment.getAppointmentDate())
                .orElseGet(() -> {
                    DoctorDailySlot newSlot = new DoctorDailySlot();
                    newSlot.setDoctor(user.getDoctor());
                    newSlot.setSlotDate(appointment.getAppointmentDate());
                    newSlot.setTotalSlots(20);
                    newSlot.setAvailableSlots(20);
                    return slotRepository.save(newSlot);
                });

        if (slot.getAvailableSlots() <= 0) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "You don't have any slots available for that day!");
            return "redirect:/doctor/re-examination/create";
        }

        slot.setAvailableSlots(slot.getAvailableSlots() - 1);
        slotRepository.save(slot);
        try {
            Appointment saved = appointmentService.saveAppointment(newAppointment);
            if (saved != null) {
                redirectAttributes.addFlashAttribute("messageType", "success");
                redirectAttributes.addFlashAttribute("message", "Appointment created successfully!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "An error occurred while saving the appointment.");
            return "redirect:/doctor/re-examination/create";
        }

        return "redirect:/doctor/home";
    }

    // method to submit finish appointment for patient
    @PostMapping("/submit")
    public String doSubmit(@RequestParam(name = "appointmentId") Integer appointmentId,
                           RedirectAttributes redirectAttributes) {
        Appointment appointment = appointmentService.findAppointmentById(appointmentId);

        if (appointment == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Appointment not found!");
            return "redirect:/doctor/home";
        }

        appointment.setStatus(AppointmentStatus.EXAMINED); // set appointment status to EXAMINED
        Appointment submitAppointment = appointmentService.submitAppointment(appointment);
        if (submitAppointment != null) {
            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Appointment submitted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Appointment not submitted!");
        }
        return "redirect:/doctor/home";
    }
}


