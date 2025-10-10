package com.group4.clinicmanagement.controller;

import com.group4.clinicmanagement.dto.UserDTO;
import com.group4.clinicmanagement.entity.Appointment;
import com.group4.clinicmanagement.entity.Doctor;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.repositories.AppointmentRepository;
import com.group4.clinicmanagement.service.AppointmentService;
import com.group4.clinicmanagement.service.CashierService;
import com.group4.clinicmanagement.service.DoctorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/cashier")
public class CashierController {
    CashierService cashierService;
    AppointmentService appointmentService;

    public CashierController(CashierService cashierService,  AppointmentService appointmentService) {
        this.cashierService = cashierService;
        this.appointmentService = appointmentService;

    }

    @GetMapping(value = "/view-profile/{id}")
    public String viewProfile(@PathVariable("id") int id, Model model) {
        User user = cashierService.findUserById(id);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getUserId());
        userDTO.setFullName(user.getFullName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhone(user.getPhone());
        userDTO.setGender(user.getGender());
        model.addAttribute("userDTO", userDTO);
        return "cashier/view-profile";
    }

    @GetMapping("/edit-profile/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
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

    @PostMapping(value = "/edit-profile/{id}")
    public String editProfile(@ModelAttribute("userDTO") UserDTO dto, @PathVariable("id") int id) {
        User user = cashierService.findUserById(id);

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setGender(dto.getGender());

        cashierService.save(user);
        return "redirect:/cashier/view-profile/" + id;
    }

    @GetMapping(value = "/view-appointment-list")
    public String viewListAppoimnent(Model model) {
        List<Appointment> appointments = appointmentService.findAll();
        model.addAttribute("appointments", appointmentService.findAll());
        return "cashier/view-appointment-list";
    }

}
