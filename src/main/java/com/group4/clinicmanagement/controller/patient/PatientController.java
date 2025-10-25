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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
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

    private String safeValue(String newValue, String oldValue) {
        if (newValue == null) return oldValue;
        String trimmed = newValue.trim();
        return trimmed.isEmpty() ? oldValue : trimmed;
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
    public String updateProfile(@Valid @ModelAttribute("patient") PatientUserDTO dto,
                                BindingResult result,
                                Model model,
                                @RequestParam("avatar") MultipartFile avatar,
                                RedirectAttributes redirect,
                                Principal principal) {
        System.out.println("DTO BEFORE MERGE: " + dto);
        String username = getCurrentUsername();
        if (result.hasErrors()) {
            // ✅ Lấy dữ liệu cũ từ DB
            PatientUserDTO oldData = patientService.getPatientsByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Patient not found"));

            // Merge dữ liệu hợp lệ + giữ dữ liệu cũ cho phần bị trống
            dto.setFullName(safeValue(dto.getFullName(), oldData.getFullName()));
            dto.setEmail(safeValue(dto.getEmail(), oldData.getEmail()));
            dto.setPhone(safeValue(dto.getPhone(), oldData.getPhone()));
            dto.setAddress(safeValue(dto.getAddress(), oldData.getAddress()));
            dto.setAvatarFilename(oldData.getAvatarFilename());
            dto.setGender(dto.getGender() != null ? dto.getGender() : oldData.getGender());
            dto.setDateOfBirth(dto.getDateOfBirth() != null ? dto.getDateOfBirth() : oldData.getDateOfBirth());
            dto.setUsername(username); // giữ lại username hiện tại

            model.addAttribute("patient", dto); // Trả lại dữ liệu cho form
            return "patient/edit-profile";
        }
        try {
            patientService.savePatientUserWithAvatar(username, dto, avatar);
            redirect.addFlashAttribute("success", "Profile updated successfully!");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
//        patientService.savePatientUserWithAvatar(username, dto, avatar);
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
