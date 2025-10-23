package com.group4.clinicmanagement.controller.patient;

import com.group4.clinicmanagement.dto.*;
import com.group4.clinicmanagement.service.LabService;
import com.group4.clinicmanagement.service.MedicalRecordService;
import com.group4.clinicmanagement.service.PatientService;
import com.group4.clinicmanagement.service.PrescriptionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null) ? authentication.getName() : null;
    }

    @GetMapping("/profile")
    public String getPatientsByUsername(Model model, HttpSession session) {
        String username = getCurrentUsername();
        PatientUserDTO patient = patientService.getPatientsByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        session.setAttribute("patientId", patient.getPatientId());
        List<MedicalRecordListDTO> medicalRecords = medicalRecordService.getMedicalRecordsByPatientId(patient.getPatientId());

        model.addAttribute("patient", patient);
        model.addAttribute("medicalRecords", medicalRecords);
        return "patient/profile";
    }

    @GetMapping("/list-medical-records")
    public String getListMedicalRecords(Model model, HttpSession session) {
        String username = getCurrentUsername();
        PatientUserDTO patient = patientService.getPatientsByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        session.setAttribute("patientId", patient.getPatientId());
        List<MedicalRecordListDTO> medicalRecords = medicalRecordService.getMedicalRecordsByPatientId(patient.getPatientId());
        model.addAttribute("medicalRecords", medicalRecords);
        return "patient/list-medical-record";
    }

    @GetMapping("/edit-profile")
    public String goToEditProfile(Model model) {
        String username = getCurrentUsername();
        PatientUserDTO dto = patientService.getPatientsByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("patient", dto);
        return "patient/edit-profile";
    }

    @PostMapping("/save-profile")
    public String updateProfile(@Valid @ModelAttribute("patient") PatientUserDTO dto, @RequestParam("avatar") MultipartFile avatar) {
        String username = getCurrentUsername();

        patientService.savePatientUserWithAvatar(username, dto, avatar);
        return "redirect:/patient/profile";
    }

    @GetMapping("/medical-records/{recordId}")
    public String getMedicalRecordsByPatientId(Model model, HttpSession session, @PathVariable("recordId") Integer recordId) {
        String username = getCurrentUsername();
        Integer patientId = (Integer) session.getAttribute("patientId");
        Optional<MedicalRecordDetailDTO> medicalRecordDetailDTO = medicalRecordService.getMedicalRecordDetailsByPatientId(patientId, recordId);
        List<PrescriptionDetailDTO> prescriptions = prescriptionService.getPrescriptionDetailsByRecordId(recordId);
        List<LabDTO> labs = labService.findLabResultByRecordId(recordId);
        model.addAttribute("prescriptions", prescriptions);
        model.addAttribute("labs", labs);
        model.addAttribute("record", medicalRecordDetailDTO.get());
        return "patient/medical-record-detail";
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm() {
        return "patient/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Xác nhận mật khẩu không khớp.");
            return "redirect:/patient/change-password";
        }

        boolean isChanged = patientService.changePassword(username, currentPassword, newPassword);

        if (!isChanged) {
            model.addAttribute("error", "Mật khẩu hiện tại không đúng.");
            return "patient/change-password";
        }

        model.addAttribute("success", "Đổi mật khẩu thành công.");
        return "redirect:/patient/profile";
    }

}
