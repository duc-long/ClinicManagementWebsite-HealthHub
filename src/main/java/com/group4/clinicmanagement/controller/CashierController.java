package com.group4.clinicmanagement.controller;

import com.group4.clinicmanagement.dto.UserDTO;
import com.group4.clinicmanagement.entity.Appointment;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.enums.AppointmentStatus;
import com.group4.clinicmanagement.enums.Gender;
import com.group4.clinicmanagement.service.AppointmentService;
import com.group4.clinicmanagement.service.CashierService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping(value = "/cashier")
public class CashierController {
    CashierService cashierService;
    AppointmentService appointmentService;

    private final int id = 4;

    public CashierController(CashierService cashierService, AppointmentService appointmentService) {
        this.cashierService = cashierService;
        this.appointmentService = appointmentService;

    }

    @GetMapping(value = "/view-profile")
    public String viewProfile( Model model) {
        User user = cashierService.findUserById(id);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(userDTO.getId());
        userDTO.setFullName(user.getFullName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhone(user.getPhone());
        user.setGender(userDTO.getGender());
        model.addAttribute("userDTO", userDTO);
        return "cashier/view-profile";
    }
    @GetMapping("/edit-profile")
    public String showEditForm( Model model) {

        User user = cashierService.findUserById(id);

        UserDTO dto = new UserDTO();
        dto.setId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setGender(user.getGender());

        model.addAttribute("userDTO", dto);
        return "cashier/edit-profile";
    }
    @PostMapping(value = "/edit-profile")
    public String editProfile(@ModelAttribute("userDTO") UserDTO dto) {

        User user = cashierService.findUserById(id);

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setGender(dto.getGender()); //fix(t)

        cashierService.save(user);
        return "redirect:/cashier/view-profile";
    }

    @GetMapping(value = "/view-appointment-list")
    public String viewListAppointment(Model model) {
        LocalDate today = LocalDate.now();
        List<Appointment> appointments = appointmentService.findByStatus(AppointmentStatus.CHECKED_IN.getValue());
        List<Appointment> todayAppointments = new ArrayList<>();

        for (Appointment appointment : appointments) {
            if (appointment.getAppointmentDate().isEqual(today)) {
                todayAppointments.add(appointment);
            }
        }
        model.addAttribute("today", today);
        model.addAttribute("todayAppointments", todayAppointments);

        return "cashier/view-appointment-list";
    }

    @GetMapping(value = "/view-appointment-detail/{id}")
    public String viewAppointmentDetail(@PathVariable("id") int id, Model model) {
        model.addAttribute("appointment", appointmentService.findById(id));
        return "cashier/view-appointment-detail";
    }

}