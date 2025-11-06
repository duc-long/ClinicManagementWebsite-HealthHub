package com.group4.clinicmanagement.controller.doctor;

import com.group4.clinicmanagement.dto.*;
import com.group4.clinicmanagement.dto.doctor.DrugCatalogDTO;
import com.group4.clinicmanagement.dto.doctor.MedicalRecordDTO;
import com.group4.clinicmanagement.dto.doctor.PrescriptionDTO;
import com.group4.clinicmanagement.dto.doctor.PrescriptionDetailDTO;
import com.group4.clinicmanagement.entity.*;
import com.group4.clinicmanagement.enums.AppointmentStatus;
import com.group4.clinicmanagement.repository.AppointmentRepository;
import com.group4.clinicmanagement.repository.DepartmentRepository;
import com.group4.clinicmanagement.service.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
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
    private final PrescriptionDetailService prescriptionDetailService;
    private final VitalSignsService vitalSignsService;
    private final LabRequestService labRequestService;
    private final PatientService patientService;

    public DoctorController(DoctorService doctorService, UserService userService, DepartmentRepository departmentRepository,
                            AppointmentRepository appointmentRepository, AppointmentService appointmentService,
                            DrugCatalogService drugCatalogService, MedicalRecordService medicalRecordService,
                            PrescriptionService prescriptionService, VitalSignsService vitalSignsService,
                            PrescriptionDetailService prescriptionDetailService, PatientService patientService,
                            LabRequestService labRequestService) {
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
        Page<AppointmentDTO> appointments = appointmentService.getTodayAppointmentsPaged(user.getUserId(), patientName, page, size);
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

    // method to show create record form
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

    // method to show update record form
    @GetMapping("/records/update/{id}")
    public String updateRecord(@PathVariable(name="id") int id, Model model, RedirectAttributes redirectAttributes) {
        MedicalRecord record = medicalRecordService.findById(id);
        MedicalRecordDTO recordDTO = new MedicalRecordDTO();
        recordDTO.setPatientId(record.getPatient().getUser().getUserId());
        recordDTO.setDoctorId(record.getDoctor().getUser().getUserId());
        recordDTO.setAppointmentId(record.getAppointment().getAppointmentId());
        recordDTO.setRecordStatus(record.getStatus());
        recordDTO.setDiagnosis(record.getDiagnosis());
        recordDTO.setNotes(record.getNotes());
        recordDTO.setRecordId(record.getRecordId());

        // check record null
        if (record == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Record not found!");
            return "redirect:/doctor/records/detail/" + id;
        }

        model.addAttribute("record", recordDTO);
        model.addAttribute("mode", "update");
        return "doctor/medical-record";
    }

    // method to do update record
    @PostMapping("/records/update")
    public String updateRecord(Model model, Principal principal,
                               @ModelAttribute("record") MedicalRecordDTO record,
                               RedirectAttributes redirectAttributes) {

        User user = userService.findUserByUsername(principal.getName());
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
    public String viewRecordDetail(@PathVariable Integer recordId, Model model) {
        // get entity form service
        MedicalRecord record = medicalRecordService.findById(recordId);
        if (record == null) {
            return "redirect:/doctor/home";
        }

        // switch to  MedicalRecordDTO
        MedicalRecordDTO recordDTO = new MedicalRecordDTO();
        recordDTO.setRecordId(record.getRecordId());
        recordDTO.setDiagnosis(record.getDiagnosis());
        recordDTO.setCreatedAt(record.getCreatedAt());
        recordDTO.setDoctorName(record.getDoctor() != null ? record.getDoctor().getUser().getFullName() : null);
        recordDTO.setNotes(record.getNotes());
        recordDTO.setRecordStatus(record.getStatus());
        recordDTO.setDoctorId(record.getDoctor() != null ? record.getDoctor().getDoctorId() : 0);
        recordDTO.setPatientId(record.getPatient() != null ? record.getPatient().getPatientId() : 0);
        recordDTO.setAppointmentId(record.getAppointment() != null ? record.getAppointment().getAppointmentId() : 0);
        recordDTO.setPatientName(record.getPatient() != null ? record.getPatient().getUser().getFullName() : null);

        // VitalSigns DTO
        VitalSigns vitalSigns = vitalSignsService.findVitalSignsById(recordId);
        VitalSignsDTO vitalSignsDTO = null;
        if (vitalSigns != null) {
            vitalSignsDTO = new VitalSignsDTO();
            vitalSignsDTO.setVitalId(vitalSigns.getVitalId());
            vitalSignsDTO.setRecordId(recordId);
            vitalSignsDTO.setHeightCm(vitalSigns.getHeightCm());
            vitalSignsDTO.setWeightKg(vitalSigns.getWeightKg());
            vitalSignsDTO.setBloodPressure(vitalSigns.getBloodPressure());
            vitalSignsDTO.setHeartRate(vitalSigns.getHeartRate());
            vitalSignsDTO.setTemperature(vitalSigns.getTemperature());
            vitalSignsDTO.setRecordedAt(vitalSigns.getRecordedAt());

            // split systolic/diastolic
            String bp = vitalSigns.getBloodPressure();
            if (bp != null && bp.contains("/")) {
                try {
                    String[] parts = bp.split("/");
                    vitalSignsDTO.setSystolic(Integer.parseInt(parts[0].trim()));
                    vitalSignsDTO.setDiastolic(Integer.parseInt(parts[1].trim()));
                } catch (Exception ignored) {}
            }
        }

        // Prescription DTO
        Prescription prescription = record.getPrescriptions();
        PrescriptionDTO prescriptionDTO = null;
        if (prescription != null) {
            prescriptionDTO = new PrescriptionDTO();
            prescriptionDTO.setPrescriptionId(prescription.getPrescriptionId());
            prescriptionDTO.setRecordId(record.getRecordId());
            prescriptionDTO.setDoctorId(prescription.getDoctor() != null ? prescription.getDoctor().getDoctorId() : 0);
            prescriptionDTO.setDoctorName(prescription.getDoctor() != null ? prescription.getDoctor().getUser().getFullName() : null);
            prescriptionDTO.setStatus(prescription.getStatus());

            // map to PrescriptionDetailDTO
            List<com.group4.clinicmanagement.dto.doctor.PrescriptionDetailDTO> detailDTOs = prescription.getDetails().stream().map(detail -> {
                PrescriptionDetailDTO prescriptionDetailDTO = new PrescriptionDetailDTO();
                prescriptionDetailDTO.setPrescriptionDetailId(detail.getDetailId());
                prescriptionDetailDTO.setPrescriptionId(prescription.getPrescriptionId());
                prescriptionDetailDTO.setDrugId(detail.getDrug() != null ? detail.getDrug().getDrugId() : 0);
                prescriptionDetailDTO.setDrugName(detail.getDrug() != null ? detail.getDrug().getName() : null);
                prescriptionDetailDTO.setDosage(detail.getDosage());
                prescriptionDetailDTO.setQuantity(detail.getQuantity() != null ? detail.getQuantity() : 0);
                prescriptionDetailDTO.setDuration(detail.getDuration_days() != null ? detail.getDuration_days() : 0);
                prescriptionDetailDTO.setInstruction(detail.getInstruction());
                prescriptionDetailDTO.setFrequency(detail.getFrequency());
                return prescriptionDetailDTO;
            }).toList();

            prescriptionDTO.setPrescriptionDetails(detailDTOs);
        }

        // 🔹 LabRequest DTO
        LabRequest labRequest = record.getLabRequests();
        LabRequestDTO labRequestDTO = null;
        if (labRequest != null) {
            labRequestDTO = new LabRequestDTO();
            labRequestDTO.setLabRequestId(labRequest.getLabRequestId());
            labRequestDTO.setMedicalRecordId(record.getRecordId());
            labRequestDTO.setLabTestCatalog(labRequest.getTest());
            labRequestDTO.setStatus(labRequest.getStatus());
            labRequestDTO.setRequestedAt(labRequest.getRequestedAt());
        }

        // 🔹 Đưa DTO sang View
        model.addAttribute("record", recordDTO);
        model.addAttribute("vitalSigns", vitalSignsDTO);
        model.addAttribute("labRequest", labRequestDTO);
        model.addAttribute("prescription", prescriptionDTO);
        model.addAttribute("section", "record-detail");
        model.addAttribute("active", "home");
        model.addAttribute("appointmentId", recordDTO.getAppointmentId());
        return "doctor/home";
    }

    // method to do create medical record
    @PostMapping("/records/create")
    public String createRecord(Model model, Principal principal,
                               @ModelAttribute("record") MedicalRecordDTO record,
                               RedirectAttributes redirectAttributes) {

        User user = userService.findUserByUsername(principal.getName());
        if (user == null) {
            return "redirect:/doctor/login";
        }

        try {
            Doctor doctor = doctorService.findDoctorById(user.getUserId());
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

            int saved = medicalRecordService.saveRecord(medicalRecord);

            redirectAttributes.addFlashAttribute("messageType", saved > 0 ? "success" : "error");
            redirectAttributes.addFlashAttribute("message", saved > 0 ?
                    "Created medical record successfully!" : "Failed to create medical record!");

            return "redirect:/doctor/home";

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
        User user = userService.findUserByUsername(principal.getName());
        if (user == null) return "redirect:/doctor/login";

        MedicalRecord record = medicalRecordService.findById(recordId);
        if (record == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Medical record not found!");
            return "redirect:/doctor/home";
        }

        // prescription DTO
        PrescriptionDTO dto = new PrescriptionDTO();
        dto.setRecordId(recordId);

        // drug DTO
        List<DrugCatalogDTO> drugCatalogDTOS = drugCatalogService.findAllDrugs()
                .stream()
                .map(drug -> {
                    DrugCatalogDTO drugDTO = new DrugCatalogDTO();
                    drugDTO.setDrugName(drug.getName());
                    drugDTO.setDrugId(drug.getDrugId());
                    return drugDTO;
                }).toList();

        model.addAttribute("mode", "create");
        model.addAttribute("record", record);
        model.addAttribute("prescription", dto);
        model.addAttribute("doctorId", user.getDoctor().getDoctorId());
        model.addAttribute("drugs", drugCatalogService.findAllDrugs());
        return "doctor/prescription-form";
    }

    // method to show prescription for update
    @GetMapping("/prescription/update/{id}")
    public String showUpdatePrescriptionForm(@PathVariable("id") int prescriptionId,
                                             Model model,
                                             Principal principal,
                                             RedirectAttributes redirectAttributes) {

        User user = userService.findUserByUsername(principal.getName());
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
        recordDTO.setDoctorName(medicalRecord.getDoctor().getUser().getFullName());
        recordDTO.setPatientName(medicalRecord.getPatient().getUser().getFullName());

        List<PrescriptionDetailDTO> detailDTOs = prescription.getDetails().stream()
                .map(detail -> {
                    PrescriptionDetailDTO dto = new PrescriptionDetailDTO();
                    dto.setPrescriptionDetailId(detail.getDetailId());
                    dto.setPrescriptionId(prescriptionId);
                    dto.setDrugId(detail.getDrug().getDrugId());
                    dto.setDrugName(detail.getDrug().getName());
                    dto.setDosage(detail.getDosage());
                    dto.setFrequency(detail.getFrequency());
                    dto.setDuration(detail.getDuration_days());
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
            @RequestParam("drugIds") List<Integer> drugIds,
            @RequestParam("quantities") List<Integer> quantities,
            @RequestParam("dosages") List<String> dosages,
            @RequestParam("frequencies") List<String> frequencies,
            @RequestParam("durationDay") List<Integer> durationDays,
            @RequestParam("instructions") List<String> instructions,
            RedirectAttributes redirectAttributes
    ) {
        try {
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

    // method to show vital sign form for update
    @GetMapping("/vitals/update/{id}")
    public String vitalSignUpdatePage(Model model, @PathVariable(name = "id") int vitalSignId,
                                      RedirectAttributes redirectAttributes) {
        VitalSigns vitalSigns = vitalSignsService.findVitalSignsById(vitalSignId);

        if (vitalSigns == null) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "VitalSigns not found!");
            return "redirect:/doctor/home";
        }

        VitalSignsDTO vitalSignsDTO = new VitalSignsDTO();
        vitalSignsDTO.setVitalId(vitalSigns.getVitalId());
        vitalSignsDTO.setWeightKg(vitalSigns.getWeightKg());
        vitalSignsDTO.setHeightCm(vitalSigns.getHeightCm());
        vitalSignsDTO.setRecordId(vitalSigns.getMedicalRecord().getRecordId());
        vitalSignsDTO.setHeartRate(vitalSigns.getHeartRate());
        vitalSignsDTO.setTemperature(vitalSigns.getTemperature());

        String[] splitBloodPressure = vitalSigns.getBloodPressure().split("/");

        try{
            vitalSignsDTO.setSystolic(Integer.parseInt(splitBloodPressure[0]));
            vitalSignsDTO.setDiastolic(Integer.parseInt(splitBloodPressure[1]));
        } catch (NumberFormatException e){
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Invalid Blood Pressure!");
            return "redirect:/doctor/home";
        }

        model.addAttribute("vital", vitalSignsDTO);
        return "doctor/vitalSign";
    }

    // method do save update vital sign
    @PostMapping("/vitals/update")
    public String saveVital(@ModelAttribute("vital") VitalSignsDTO vital,
                            @RequestParam("recordId") int recordId,
                            RedirectAttributes redirectAttributes) {

        vitalSignsService.saveOrUpdate(recordId, vital);
        redirectAttributes.addFlashAttribute("messageType", "success");
        redirectAttributes.addFlashAttribute("message", "Vital signs saved successfully!");

        return "redirect:/doctor/records/detail/" + recordId;
    }

    // method to show lab request fragment
    @GetMapping("/labs/create")
    public String labCreatePage(Model model) {
        model.addAttribute(new LabRequestDTO());
        model.addAttribute("section", "lab-request");
        model.addAttribute("active", "labs");
        return "doctor/home";
    }

    // method to submit finish appointment for patient
    @PostMapping("/submit")
    public String doSubmit(@RequestParam(name="appointmentId") Integer appointmentId,
            RedirectAttributes redirectAttributes) {
        Appointment appointment  = appointmentService.findAppointmentById(appointmentId);

        if  (appointment == null) {
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


