package com.group4.clinicmanagement.controller;

import com.group4.clinicmanagement.dto.UserDTO;
import com.group4.clinicmanagement.entity.*;
import com.group4.clinicmanagement.enums.AppointmentStatus;
import com.group4.clinicmanagement.enums.Gender;
import com.group4.clinicmanagement.service.AppointmentService;
import com.group4.clinicmanagement.service.ReceptionistService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/receptionist")
public class ReceptionistController {

    private final ReceptionistService receptionistService;
    private final AppointmentService appointmentService;

    public ReceptionistController(ReceptionistService receptionistService,
                                  AppointmentService appointmentService) {
        this.receptionistService = receptionistService;
        this.appointmentService = appointmentService;
    }

    // ===================== PROFILE =====================
    @GetMapping("/profile")
    public String viewReceptionistProfile(Model model) {
        int receptionistId = 4; // Hardcoded for demo
        User user = receptionistService.findUserById(receptionistId);

        UserDTO dto = new UserDTO(
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getGender(),
                user.getUserId()
        );

        model.addAttribute("receptionist", dto);
        return "receptionist/profile";
    }

    @GetMapping("/edit-profile")
    public String showEditForm(Model model) {
        int receptionistId = 4;
        User user = receptionistService.findUserById(receptionistId);

        UserDTO dto = new UserDTO(
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getGender(),
                user.getUserId()
        );

        model.addAttribute("receptionist", dto);
        return "receptionist/edit-profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("receptionist") UserDTO dto, Model model) {
        User receptionist = receptionistService.findUserById(dto.getId());
        receptionist.setFullName(dto.getFullName());
        receptionist.setEmail(dto.getEmail());
        receptionist.setPhone(dto.getPhone());
        receptionist.setGender(dto.getGender());

        receptionistService.save(receptionist);

        model.addAttribute("receptionist", dto);
        model.addAttribute("success", "Receptionist profile updated successfully.");
        return "receptionist/profile";
    }

    // ===================== APPOINTMENTS =====================
    @GetMapping("/appointments")
    public String listAppointments(Model model) {
        List<Appointment> appointments = appointmentService.getAllAppointments();
        model.addAttribute("appointments", appointments);
        return "receptionist/appointment-list";
    }

    @GetMapping("/appointments/{id}")
    public String viewAppointmentDetails(@PathVariable("id") Integer id, Model model) {
        Appointment appointment = appointmentService.getById(id);
        model.addAttribute("appointment", appointment);
        return "receptionist/appointment-details";
    }

}
