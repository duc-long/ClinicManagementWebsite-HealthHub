package com.group4.clinicmanagement.controller.receptionist;

import com.group4.clinicmanagement.dto.RecepCashAppointmentDTO;
import com.group4.clinicmanagement.dto.ReceptionistUserDTO;
import com.group4.clinicmanagement.entity.Appointment;
import com.group4.clinicmanagement.entity.Department;
import com.group4.clinicmanagement.entity.Doctor;
import com.group4.clinicmanagement.entity.Staff;
import com.group4.clinicmanagement.enums.AppointmentStatus;
import com.group4.clinicmanagement.repository.StaffRepository;
import com.group4.clinicmanagement.service.DepartmentService;
import com.group4.clinicmanagement.service.ReceptionistService;
import com.group4.clinicmanagement.service.AppointmentService;
import com.group4.clinicmanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/receptionist")
public class ReceptionistController {

    private final ReceptionistService receptionistService;
    private final AppointmentService appointmentService;
    private final DepartmentService departmentService;
    private final StaffRepository staffRepository;
    private final UserService userService;

    public ReceptionistController(ReceptionistService receptionistService, UserService userService, AppointmentService appointmentService, DepartmentService departmentService, StaffRepository staffRepository) {
        this.receptionistService = receptionistService;
        this.appointmentService = appointmentService;
        this.departmentService = departmentService;
        this.staffRepository = staffRepository;
        this.userService = userService;
    }


    @GetMapping("/profile")
    public String viewProfile(Model model, Authentication authentication) {
        String receptionistName = authentication.getName();
        ReceptionistUserDTO dto = receptionistService.getReceptionistProfile(receptionistName);
        model.addAttribute("receptionist", dto);
        return "receptionist/profile";
    }

    @GetMapping("/edit-profile")
    public String editProfile(Model model, Authentication authentication) {
        String receptionistName = authentication.getName();
        ReceptionistUserDTO dto = receptionistService.getReceptionistProfile(receptionistName);
        model.addAttribute("receptionist", dto);
        return "receptionist/edit-profile";
    }

    @PostMapping("/edit-profile")
    public String editProfile(
            @Valid @ModelAttribute("receptionist") ReceptionistUserDTO dto,
            BindingResult bindingResult,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("receptionist", dto);
            return "receptionist/edit-profile";
        }

        try {
            String receptionistName = authentication.getName();
            receptionistService.updateReceptionistProfile(receptionistName, dto, avatarFile);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            return "redirect:/receptionist/profile";

        } catch (IllegalArgumentException e) {
            model.addAttribute("fileError", e.getMessage());
            return "receptionist/edit-profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unexpected error while saving profile.");
            return "redirect:/receptionist/profile";
        }
    }

    @GetMapping("/appointment-list")
    public String listAppointments(@RequestParam(required = false, defaultValue = "ALL") String status,
                                   @RequestParam(defaultValue = "1") String page,
            @RequestParam(defaultValue = "10") int size,Model model) {
        Page<RecepCashAppointmentDTO> appointments;
        int currentPage = 1;
        try {
            currentPage = Integer.parseInt(page);
            if (currentPage < 1) currentPage = 1;
        } catch (NumberFormatException e) {
            currentPage = 1;
        }
        switch (status.toUpperCase()){
            case "PENDING" -> {
                appointments = appointmentService.getStatusAppointmentPage(AppointmentStatus.PENDING.getValue(), currentPage, size);
            }
            case "CONFIRMED" -> {
                appointments = appointmentService.getStatusAppointmentPage(AppointmentStatus.CONFIRMED.getValue(), currentPage, size);
            }
            case "CHECKED_IN" -> {
                appointments = appointmentService.getStatusAppointmentPage(AppointmentStatus.CHECKED_IN.getValue(), currentPage, size);
            }
            case "NO_SHOW" -> {
                appointments = appointmentService.getStatusAppointmentPage(AppointmentStatus.NO_SHOW.getValue(), currentPage, size);
            }
            case "CANCEL" ->{
                appointments = appointmentService.getStatusAppointmentPage(AppointmentStatus.CANCELLED.getValue(), currentPage,size);
            }
            default ->   appointments = appointmentService.getStatusAppointmentPage(AppointmentStatus.PENDING.getValue(), currentPage, size);
        }
        model.addAttribute("appointments", appointments);
        if(currentPage > appointments.getTotalPages()){
            currentPage =1;
        }
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("activeStatus", status.toUpperCase());
        return "receptionist/appointment-list";
    }

    // method to show change password
    @GetMapping("/change-password")
    public String changePassword(Model model, Principal principal) {
        Staff user = userService.findUserByUsername(principal.getName());

        if (user == null) {
            return "redirect:/receptionist/login";
        }

        model.addAttribute("active", "profile");
        model.addAttribute("section", "change-password");
        return "receptionist/change-password";
    }

    // method to change doctor password
    @PostMapping("/change-password")
    public String changePassword(RedirectAttributes redirectAttributes, Principal principal,
                                 @RequestParam(name = "currentPassword") String currentPassword,
                                 @RequestParam(name = "newPassword") String newPassword,
                                 @RequestParam(name = "confirmPassword", required = false) String confirmPassword) {
        Staff user = userService.findUserByUsername(principal.getName());
        if (user == null) {
            return "redirect:/receptionist/login";
        }

        // basic server-side checks
        if (confirmPassword == null || !newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "New password and confirm password do not match!");
            return "redirect:/receptionist/change-password";
        }

        boolean changed = userService.changePassword(user, currentPassword, newPassword);
        if (changed) {
            // clean login information
            SecurityContextHolder.clearContext();
            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Password changed successfully. Please log in again.");
            return "redirect:/receptionist/login";
        } else {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Change password failed. Check your current password or password policy.");
            return "redirect:/receptionist/change-password";
        }
    }

    @GetMapping("/appointment/{id}")
    public String viewAppointmentDetails(@PathVariable("id") String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            int appointmentId = Integer.parseInt(id);
            Appointment appointment = appointmentService.getAppointmentForReceptionist(appointmentId);
            if (appointment == null) {
                redirectAttributes.addFlashAttribute("message", "Appointment not found");
                redirectAttributes.addFlashAttribute("messageType", "error");
                return "redirect:/receptionist/appointment-list";

            }
            model.addAttribute("appointment", appointment);
            return "receptionist/appointment-details";

        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("message", "Invalid appointment ID format.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/receptionist/appointment-list";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "An unexpected error occurred during check-in.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/receptionist/appointment-list";

        }
    }

    @GetMapping("/appointment/schedule/{id}")
    public String showEditAppointmentForm(
            @PathVariable("id") String id,
            @RequestParam(value = "departmentId", required = false) String departmentIdParam,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            int appointmentId = Integer.parseInt(id);
            Appointment appointment = appointmentService.getById(appointmentId);
            if (appointment == null) {
                redirectAttributes.addFlashAttribute("message", "Appointment not found.");
                redirectAttributes.addFlashAttribute("messageType", "error");
                return "redirect:/receptionist/appointment-list";
            }
            List<Department> departments = departmentService.getAllDepartment();
            List<Doctor> doctorList = new ArrayList<>();
            Integer departmentId = null;
            if (departmentIdParam != null && !departmentIdParam.isBlank()) {
                try {
                    departmentId = Integer.parseInt(departmentIdParam);
                    boolean found = false;
                    for (Department dept : departments) {
                        if (dept.getDepartmentId() == departmentId) {
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        redirectAttributes.addFlashAttribute("message", "Invalid department ID.");
                        redirectAttributes.addFlashAttribute("messageType", "error");
                        return "redirect:/receptionist/appointment-list";
                    }

                    doctorList = appointmentService.getAvailableDoctors(departmentId, appointment.getAppointmentDate());
                } catch (NumberFormatException e) {
                    redirectAttributes.addFlashAttribute("message", "Invalid department ID format.");
                    redirectAttributes.addFlashAttribute("messageType", "error");
                    return "redirect:/receptionist/appointment-list";
                }
            }

            model.addAttribute("appointment", appointment);
            model.addAttribute("department", departments);
            model.addAttribute("selectedDepartmentId", departmentId);
            model.addAttribute("availableDoctors", doctorList);
            model.addAttribute("statuses", AppointmentStatus.values());

            return "receptionist/appointment-edit";

        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("message", "Invalid appointment ID format.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/receptionist/appointment-list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "An unexpected error occurred while loading the appointment form.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/receptionist/appointment-list";
        }
    }


    @PostMapping("/appointment/schedule")
    public String updateAppointment(@ModelAttribute("appointment") Appointment updatedAppointment, RedirectAttributes redirectAttributes, Principal principal) {
        try {
            if (updatedAppointment.getStatusValue() != null) {
                updatedAppointment.setStatus(AppointmentStatus.fromInt(updatedAppointment.getStatusValue()));
            }
            String username = principal.getName();
            Staff receptionist = staffRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Receptionist not found: " + username));
            appointmentService.scheduleAppointment(updatedAppointment, receptionist);

            redirectAttributes.addFlashAttribute("message", "Appointment scheduled successfully!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "An unexpected error occurred during check-in.");
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/receptionist/appointment-list";
    }

    @PostMapping("/appointment/checkin/{id}")
    public String checkInAppointment(@PathVariable("id") int appointmentId,
                                     RedirectAttributes redirectAttributes) {
        try {
            appointmentService.checkInAppointment(appointmentId);
            redirectAttributes.addFlashAttribute("message", "Patient checked in successfully!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "An unexpected error occurred during check-in.");
            redirectAttributes.addFlashAttribute("messageType", "error");
        }

        return "redirect:/receptionist/appointment-list?status=CONFIRMED";
    }



}

