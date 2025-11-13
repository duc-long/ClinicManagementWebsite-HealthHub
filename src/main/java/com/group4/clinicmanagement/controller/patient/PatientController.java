package com.group4.clinicmanagement.controller.patient;

import com.group4.clinicmanagement.dto.*;
import com.group4.clinicmanagement.entity.Patient;
import com.group4.clinicmanagement.entity.Staff;
import com.group4.clinicmanagement.repository.PatientRepository;
import com.group4.clinicmanagement.repository.StaffRepository;
import com.group4.clinicmanagement.security.CustomUserDetails;
import com.group4.clinicmanagement.service.LabService;
import com.group4.clinicmanagement.service.MedicalRecordService;
import com.group4.clinicmanagement.service.PatientService;
import com.group4.clinicmanagement.service.PrescriptionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private PatientRepository patientRepository;

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
    public String getPatientsByUsername(Model model, HttpSession session, HttpServletRequest request) {
        String username = getCurrentUsername();
        PatientUserDTO patient = patientService.getPatientsByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        session.setAttribute("patientId", patient.getPatientId());
        List<MedicalRecordListDTO> medicalRecords = medicalRecordService.getMedicalRecordsByPatientId(patient.getPatientId());

        model.addAttribute("patient", patient);
        model.addAttribute("medicalRecords", medicalRecords);
        model.addAttribute("currentPath", request.getRequestURI());
        return "patient/profile";
    }

    @GetMapping("/list-medical-records")
    public String getListMedicalRecords(Model model, HttpSession session, HttpServletRequest request) {
        String username = getCurrentUsername();
        PatientUserDTO patient = patientService.getPatientsByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        session.setAttribute("patientId", patient.getPatientId());
        List<MedicalRecordListDTO> medicalRecords = medicalRecordService.getMedicalRecordsByPatientId(patient.getPatientId());
        model.addAttribute("medicalRecords", medicalRecords);
        model.addAttribute("currentPath", request.getRequestURI());
        return "patient/list-medical-record";
    }

    @GetMapping("/edit-profile")
    public String goToEditProfile(Model model, HttpServletRequest request) {
        String username = getCurrentUsername();
        PatientUserDTO dto = patientService.getPatientsByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("patient", dto);
        model.addAttribute("currentPath", request.getRequestURI());
        return "patient/edit-profile";
    }

    @PostMapping("/save-profile")
    public String updateProfile(@Valid @ModelAttribute("patient") PatientUserDTO dto,
                                BindingResult result,
                                Model model,
                                @RequestParam("avatar") MultipartFile avatar,
                                RedirectAttributes redirect) {
        String username = getCurrentUsername();

        Optional<Patient> existing = patientRepository.findByEmail(dto.getEmail());
        if (existing.isPresent() && !existing.get().getUsername().equals(username)) {
            // Email đã tồn tại và không phải của người dùng hiện tại
            result.rejectValue("email", "error.email", "Email is already in use by someone else.");
        }

        if (result.hasErrors()) {
            PatientUserDTO oldData = patientService.getPatientsByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Patient not found"));

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
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", "An unexpected error occurred while saving your profile.");
        }
        return "redirect:/patient/profile";
    }

    @GetMapping("/medical-records/{recordId}")
    public String getMedicalRecordsByPatientId(Model model,
                                               @PathVariable("recordId") String recordIdStr,
                                               HttpServletRequest request,
                                               @AuthenticationPrincipal CustomUserDetails userDetails,
                                               RedirectAttributes redirect) {
        try {
            Integer recordId = Integer.parseInt(recordIdStr);
            Integer userId = userDetails.getUserId();

            Optional<MedicalRecordDetailDTO> medicalRecordDetailDTO =
                    medicalRecordService.getMedicalRecordDetailsByPatientId(userId, recordId);

            if (!medicalRecordDetailDTO.isPresent()) {
                redirect.addFlashAttribute("error", "Medical record not found or access denied.");
                return "redirect:/patient/list-medical-records";
//                throw new RuntimeException("Medical record not found or access denied.");
            }

            List<PrescriptionDetailDTO> prescriptions = prescriptionService.getPrescriptionDetailsByRecordId(recordId);
            List<LabDTO> labs = labService.findLabResultByRecordId(recordId);

            model.addAttribute("prescriptions", prescriptions);
            model.addAttribute("labs", labs);
            model.addAttribute("record", medicalRecordDetailDTO.get());
            model.addAttribute("currentPath", request.getRequestURI());

            return "patient/medical-record-detail";

        } catch (NumberFormatException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/patient/list-medical-records?error=invalidId";
        } catch (Exception e) {
            // Nếu có lỗi khác, thêm thông báo lỗi vào flash attributes và chuyển hướng
            redirect.addFlashAttribute("error", "An error occurred: " + e.getMessage());
            return "redirect:/patient/list-medical-records";
        }
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model, HttpServletRequest request) {

        model.addAttribute("currentPath", request.getRequestURI());return "patient/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        boolean hasError = false;

        // Check rỗng
        if (currentPassword == null || currentPassword.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Change password failed.");
            model.addAttribute("currentPasswordError", "Current password must not be blank.");
            hasError = true;
        }

        if (newPassword == null || newPassword.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Change password failed.");
            model.addAttribute("newPasswordError", "New password must not be blank.");
            hasError = true;
        } else {
            // Kiểm tra pattern phức tạp
            String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,50}$";
            if (!newPassword.matches(pattern)) {
                redirectAttributes.addFlashAttribute("error", "Change password failed.");
                model.addAttribute("newPasswordError", "Password must contain upper, lower, number, and special character (8-50 chars).");
                hasError = true;
            }
        }

        if (confirmPassword == null || confirmPassword.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Change password failed.");
            model.addAttribute("confirmError", "Confirm password must not be blank.");
            hasError = true;
        } else if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Password confirmation does not match.");
            model.addAttribute("confirmError", "Password confirmation does not match.");
            hasError = true;
        }

        // Nếu lỗi ở form → hiển thị lại form với lỗi
        if (hasError) {
            return "patient/change-password";
        }

        // Gọi service để đổi
        boolean changed = patientService.changePassword(username, currentPassword, newPassword);
        if (!changed) {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect.");
            model.addAttribute("currentPasswordError", "Current password is incorrect.");
            return "patient/change-password";
        }

        // Thành công → redirect
        redirectAttributes.addFlashAttribute("success", "Password changed successfully.");
        return "redirect:/patient/profile";
    }

}
