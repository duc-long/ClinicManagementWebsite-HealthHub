package com.group4.clinicmanagement.controller.doctor;

import com.group4.clinicmanagement.dto.*;
import com.group4.clinicmanagement.dto.doctor.MedicalRecordDTO;
import com.group4.clinicmanagement.dto.doctor.PrescriptionDTO;
import com.group4.clinicmanagement.entity.*;
import com.group4.clinicmanagement.enums.AppointmentStatus;
import com.group4.clinicmanagement.enums.PrescriptionStatus;
import com.group4.clinicmanagement.repository.AppointmentRepository;
import com.group4.clinicmanagement.repository.DepartmentRepository;
import com.group4.clinicmanagement.service.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

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

    public DoctorController(DoctorService doctorService, UserService userService, DepartmentRepository departmentRepository,
                            AppointmentRepository appointmentRepository, AppointmentService appointmentService,
                            DrugCatalogService drugCatalogService, MedicalRecordService medicalRecordService,
                            PrescriptionService prescriptionService) {
        this.doctorService = doctorService;
        this.userService = userService;
        this.departmentRepository = departmentRepository;
        this.appointmentRepository = appointmentRepository;
        this.appointmentService = appointmentService;
        this.drugCatalogService = drugCatalogService;
        this.medicalRecordService = medicalRecordService;
        this.prescriptionService = prescriptionService;
    }

    // method to load home view doctor
    @GetMapping("/home")
    public String home(Model model,
                       Principal principal,
                       @RequestParam(value = "patientName", required = false) String patientName,
                       @RequestParam(value = "status", required = false) AppointmentStatus status,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "size", defaultValue = "10") int size) {
        User user = userService.findUserByUsername(principal.getName());

        // check user null
        if (user == null) {
            return "redirect:/doctor/login";
        }

        model.addAttribute("todayCount", appointmentService.countTodayAppointments(user.getUserId()));
        model.addAttribute("waitingCount", appointmentService.countWaitingAppointments(user.getUserId()));

        Page<AppointmentDTO> appointments = appointmentService.getTodayAppointmentsPaged(user.getUserId(), patientName, status, page, size);
        model.addAttribute("appointments", appointments.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", appointments.getTotalPages());
        model.addAttribute("patientName", patientName);
        model.addAttribute("status", status);
        model.addAttribute("section", "home");
        model.addAttribute("active", "home");

        return "doctor/home";
    }

    // method to load profile doctor
    @GetMapping("/profile")
    public String loadProfile(Principal principal, Model model) {
        User user = userService.findUserByUsername(principal.getName());
        if (user == null) return "redirect:/doctor/login";

        Doctor doctor = doctorService.findDoctorById(user.getUserId());
        model.addAttribute("doctor", doctor);

        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setGender(user.getGender());
        doctorDTO.setEmail(user.getEmail());
        doctorDTO.setPhone(user.getPhone());
        doctorDTO.setFullName(user.getFullName());
        doctorDTO.setDoctorId(user.getUserId());
        doctorDTO.setAvatarFileName(user.getAvatar());
        doctorDTO.setUsername(user.getUsername());
        Department department = departmentRepository.findByDepartmentId(doctor.getDepartment().getDepartmentId())
                .orElse(null);
        model.addAttribute("doctor", doctorDTO);
        model.addAttribute("department", department.getName());
        model.addAttribute("section", "profile");
        return "doctor/home";
    }

    // method to load appointment list for doctor
    @GetMapping("/appointments")
    public String loadAppointment(Principal principal, Model model, RedirectAttributes redirectAttributes) {
        User user = userService.findUserByUsername(principal.getName());
        if (user == null) return "redirect:/doctor/login";

        Doctor doctor = doctorService.findDoctorById(user.getUserId());
        model.addAttribute("doctor", doctor);

        try {
            List<AppointmentDTO> appointmentList = appointmentRepository
                    .findByDoctor_DoctorIdAndStatusValue(user.getUserId(), AppointmentStatus.CHECKED_IN.getValue())
                    .stream()
                    .map(a -> new AppointmentDTO(
                            a.getAppointmentId(),
                            a.getPatient().getPatientId(),
                            a.getDoctor() != null && a.getDoctor().getUser() != null ? a.getDoctor().getUser().getFullName() : "Unknown",
                            a.getPatient() != null && a.getPatient().getUser() != null ? a.getPatient().getUser().getFullName() : "Unknown",
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
    public String loadAppointmentDetail(@PathVariable int id, Model model,
                                        RedirectAttributes redirectAttributes,
                                        Principal principal) {
        Appointment a = appointmentService.findById(id);
        User user = userService.findUserByUsername(principal.getName());

        if (user.getUserId() != a.getDoctor().getDoctorId()) {
            redirectAttributes.addFlashAttribute("message", "You can't access this appointment");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/doctor/home";
        }

        MedicalRecord medicalRecord = medicalRecordService.findByAppointmentId(id);
        System.out.println("medicalRecord = " + medicalRecord.getPatient().getUser().getFullName());
        Integer medicalRecordId = (medicalRecord != null) ? medicalRecord.getRecordId() : null;

        AppointmentDTO dto = new AppointmentDTO(
                a.getAppointmentId(),
                a.getPatient().getPatientId(),
                a.getDoctor().getUser().getFullName(),
                a.getPatient().getUser().getFullName(),
                a.getReceptionist() != null ? a.getReceptionist().getFullName() : "N/A",
                a.getAppointmentDate(),
                a.getCreatedAt(),
                a.getStatus(),
                a.getQueueNumber(),
                a.getNotes(),
                a.getCancelReason()
        );

        model.addAttribute("appointment", dto);
        model.addAttribute("section", "appointment-detail");
        model.addAttribute("active", "home");
        model.addAttribute("recordId", medicalRecordId);
        return "doctor/home";
    }

    // method to redirect to update profile page
    @GetMapping("/profile/edit/{id}")
    public String loadProfile(Model model,
                              @PathVariable(name = "id") int id,
                              Principal principal) {
        User user = userService.findUserByUsername(principal.getName());

        // check valid user info
        if (user.getUserId() != id) {
            return "redirect:/doctor/login";
        }

        Doctor doctor = doctorService.findDoctorById(id);
        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setGender(user.getGender());
        doctorDTO.setEmail(user.getEmail());
        doctorDTO.setPhone(user.getPhone());
        doctorDTO.setFullName(user.getFullName());
        doctorDTO.setDoctorId(user.getUserId());
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

        User user = userService.findUserByUsername(principal.getName());
        if (user == null) return "redirect:/login";

        Doctor doctor = doctorService.findDoctorById(user.getUserId());
        if (doctor == null) return "redirect:/doctor/home";

        // --- Update user basic info ---
        user.setFullName(doctorModel.getFullName());
        user.setUsername(doctorModel.getUsername());
        user.setEmail(doctorModel.getEmail());
        user.setPhone(doctorModel.getPhone());
        user.setGender(doctorModel.getGender());
        userService.saveUser(user);

        // --- Update doctor-specific info ---
        doctor.setBio(doctorModel.getBio());
        doctorService.saveDoctor(doctor);

        redirectAttributes.addFlashAttribute("message", "Doctor profile updated successfully!");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/doctor/home";
    }

    // method to show record form
    @GetMapping("/records/{id}")
    public String loadRecordForm(Model model, Principal principal,
                                 @RequestParam(name = "patientId") int patientId,
                                 @RequestParam(name = "appointmentId") int appointmentId) {
        User user = userService.findUserByUsername(principal.getName());
        if (user == null) return "redirect:/doctor/login";

        model.addAttribute("record", new MedicalRecordDTO());
        model.addAttribute("appointmentId", appointmentId);
        model.addAttribute("patientId", patientId);
        model.addAttribute("active", "home");
        model.addAttribute("section", "create-record");
        return "doctor/home";
    }

    // method to show list medical record
    @GetMapping("/records/list")
    public String loadRecordList(Model model, Principal principal) {
        User user = userService.findUserByUsername(principal.getName());
        if (user == null) return "redirect:/doctor/login";

        List<MedicalRecordDTO> medicalRecordListDTOList =
                medicalRecordService.findMedicalRecordByDoctorIdAndStatus(user.getUserId())
                        .stream()
                        .map(medicalRecord -> new MedicalRecordDTO(
                                medicalRecord.getRecordId(),
                                medicalRecord.getDiagnosis(),
                                medicalRecord.getCreatedAt(),
                                medicalRecord.getDoctor().getUser().getFullName(),
                                medicalRecord.getNotes(),
                                medicalRecord.getStatus(),
                                medicalRecord.getDoctor().getDoctorId(),
                                medicalRecord.getPatient().getPatientId(),
                                medicalRecord.getAppointment().getAppointmentId(),
                                medicalRecord.getPatient().getUser().getFullName()
                        )).toList();

        model.addAttribute("records", medicalRecordListDTOList);
        model.addAttribute("section", "medicalRecordList");
        return "doctor/home";
    }

    // method to save medical record
    @PostMapping("/record/save")
    public String saveRecord(Model model, Principal principal,
                             @ModelAttribute(name = "record") MedicalRecordDTO record,
                             RedirectAttributes redirectAttributes) {
        User user = userService.findUserByUsername(principal.getName());
        Doctor doctor = doctorService.findDoctorById(user.getUserId());

        Appointment appointment = new Appointment();
        appointment.setAppointmentId(record.getAppointmentId());

        Patient patient = new Patient();
        patient.setPatientId(record.getPatientId());

        // Create MedicalRecord
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setDoctor(doctor);
        medicalRecord.setAppointment(appointment);
        medicalRecord.setPatient(patient);
        medicalRecord.setDiagnosis(record.getDiagnosis());
        medicalRecord.setNotes(record.getNotes());
        medicalRecord.setStatus(record.getRecordStatus());
        medicalRecord.setCreatedBy(user);

        // save to DB
        int saved = medicalRecordService.saveRecord(medicalRecord);

        if (saved > 0) {
            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Create medical record successfully!");
            redirectAttributes.addFlashAttribute("record", record);
            return "redirect:/doctor/vital/create";
        } else {
            model.addAttribute("messageType", "error");
            model.addAttribute("message", "Failed to save medical record!");
            model.addAttribute("section", "appointments");
            return "/doctor/home";
        }
    }

    @GetMapping("/prescription/new/{id}")
    public String loadCreatePrescription(@PathVariable("id") int recordId,
                                         @RequestParam("appointmentId") int appointmentId,
                                         @RequestParam("patientId") int patientId,
                                         Model model, Principal principal) {
        User user = userService.findUserByUsername(principal.getName());
        if (user == null) return "redirect:/doctor/login";

        MedicalRecord medicalRecord = medicalRecordService.findById(recordId);
        MedicalRecordDTO medicalRecordDTO = new MedicalRecordDTO();

        medicalRecordDTO.setPatientId(patientId);
        medicalRecordDTO.setDiagnosis(medicalRecord.getDiagnosis());
        medicalRecordDTO.setPatientName(medicalRecord.getPatient().getUser().getFullName());

        model.addAttribute("recordId", recordId);
        model.addAttribute("record", medicalRecordDTO);
        model.addAttribute("prescription", new PrescriptionDTO());
        model.addAttribute("drugs", drugCatalogService.findAllDrugs());
        return "doctor/create-prescription";
    }


    @PostMapping("/prescription/save")
    @Transactional
    public String savePrescription(@RequestParam("recordId") int recordId,
                                   @RequestParam("drugIds") List<Integer> drugIds,
                                   @RequestParam("quantities") List<Integer> quantities,
                                   @RequestParam("dosages") List<String> dosages,
                                   @RequestParam("frequencies") List<Integer> frequencies,
                                   @RequestParam("durationDay") List<Integer> durationDays,
                                   @RequestParam("instructions") List<String> instructions,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findUserByUsername(principal.getName());
            if (user == null) {
                redirectAttributes.addFlashAttribute("messageType", "error");
                redirectAttributes.addFlashAttribute("message", "Doctor not found or session expired!");
                return "redirect:/doctor/login";
            }

            Doctor doctor = doctorService.findDoctorById(user.getUserId());

            prescriptionService.savePrescription(
                    recordId,
                    doctor.getDoctorId(),
                    PrescriptionStatus.ACTIVE,
                    drugIds, quantities, dosages, frequencies, durationDays, instructions
            );

            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Prescription created successfully!");
            return "redirect:/doctor/home";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Failed to create prescription: " + e.getMessage());
            return "redirect:/doctor/records/list";
        }
    }


    @GetMapping("/vital/create")
    public String showVitalForm(Model model, Principal principal, @ModelAttribute(value = "record") MedicalRecordDTO record) {
        User user = userService.findUserByUsername(principal.getName());
        if (user == null) return "redirect:/doctor/login";

        model.addAttribute("vitalDTO", new VitalSignsDTO());
        model.addAttribute("section", "vital-create");
        model.addAttribute("active", "home");
        return "doctor/home";
    }

}

