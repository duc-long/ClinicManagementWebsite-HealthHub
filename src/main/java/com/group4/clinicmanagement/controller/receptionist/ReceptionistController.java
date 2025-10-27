package com.group4.clinicmanagement.controller.receptionist;

import com.group4.clinicmanagement.dto.ReceptionistUserDTO;
import com.group4.clinicmanagement.dto.UserDTO;
import com.group4.clinicmanagement.entity.Appointment;
import com.group4.clinicmanagement.entity.Doctor;
import com.group4.clinicmanagement.enums.AppointmentStatus;
import com.group4.clinicmanagement.service.ReceptionistService;
import com.group4.clinicmanagement.service.AppointmentService;
import org.springframework.security.core.Authentication;
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

    @GetMapping("/home")
    public String home() {
        return "receptionist/receptionist-home";
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
    public String editProfile(@ModelAttribute("receptionist") ReceptionistUserDTO dto, Authentication authentication) {
        String receptionistName = authentication.getName();

        receptionistService.updateReceptionistProfile(receptionistName, dto);
        return "redirect:/receptionist/profile";
    }


    @GetMapping("/appointment-list")
    public String listAppointments(Model model) {
        List<Appointment> appointments = appointmentService.getAllAppointments();
        model.addAttribute("appointments", appointments);
        return "receptionist/appointment-list";
    }

    @GetMapping("/appointment/{id}")
    public String viewAppointmentDetails(@PathVariable("id") Integer id, Model model) {
        Appointment appointment = appointmentService.getById(id);
        model.addAttribute("appointment", appointment);
        return "receptionist/appointment-details";
    }

    @GetMapping("/appointment/edit/{id}")
    public String showEditAppointmentForm(@PathVariable("id") Integer id, Model model) {
        Appointment appointment = appointmentService.getById(id);
        List<Doctor> availableDoctors = appointmentService.getAvailableDoctors(appointment.getAppointmentDate());
        model.addAttribute("appointment", appointment);
        model.addAttribute("availableDoctors", availableDoctors);
        model.addAttribute("statuses", AppointmentStatus.values());
        return "receptionist/appointment-edit";
    }

    @PostMapping("/appointment/edit")
    public String updateAppointment(@ModelAttribute("appointment") Appointment updatedAppointment) {
        if (updatedAppointment.getStatusValue() != null) {
            updatedAppointment.setStatus(AppointmentStatus.fromInt(updatedAppointment.getStatusValue()));
        }

        appointmentService.updateAppointment(updatedAppointment);
        return "redirect:/receptionist/appointment-list";
    }
}

