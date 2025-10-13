package com.group4.clinicmanagement.controller;


import com.group4.clinicmanagement.entity.Appointment;
import com.group4.clinicmanagement.entity.Patient;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.enums.AppointmentStatus;
import com.group4.clinicmanagement.repository.UserRepository;
import com.group4.clinicmanagement.service.AppointmentService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.beans.Transient;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Controller
@RequestMapping("/appointment")
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final UserRepository userRepository;

    public AppointmentController(AppointmentService appointmentService, UserRepository userRepository) {
        this.appointmentService = appointmentService;
        this.userRepository = userRepository;
    }

    @RequestMapping("/appointment-list")
    public String appointmentPage(Model model, HttpSession session) {
//        User currentUser = (User) session.getAttribute("loggedUser");

        User currentUser = userRepository.findByUserId(11).orElse(null);
        if (currentUser == null) return "redirect:/login";

        List<Appointment> all = appointmentService.findAllByPatientId(currentUser.getUserId());

        // filter list
        List<Appointment> active = all.stream()
                .filter(a -> a.getStatus().getValue() == 5)
                .toList();
        List<Appointment> history = all.stream()
                .filter(a -> a.getStatus().getValue() == 3)
                .toList();
        List<Appointment> submit = all.stream()
                .filter(a -> a.getStatus().getValue() == 0 || a.getStatus().getValue() == 1)
                .toList();

        model.addAttribute("appointments", active);
        model.addAttribute("historyList", history);
        model.addAttribute("submitList", submit);
        model.addAttribute("appointment", new Appointment());
        return "patient/appointment-dashboard";
    }

    @GetMapping("/detail/{id}")
    public String viewAppointmentDetail(@PathVariable("id") int id, Model model, HttpSession session) {
//        User currentUser = (User) session.getAttribute("user");
        User currentUser = userRepository.findByUserId(11).orElse(null);
        if (currentUser == null) {
            return "redirect:/login";
        }

        Appointment appointment = appointmentService.findAppointmentById(id);
        if (appointment == null || appointment.getPatient().getPatientId() != currentUser.getUserId()) {
            return "redirect:/patient/appointments";
        }

        model.addAttribute("appointment", appointment);
        return "patient/appointment-detail";
    }

    @GetMapping("/make-appointment")
    public String makeAppointment(Model model) {
        model.addAttribute("appointment", new Appointment());
        return "/patient/make-appointment";
    }

    @Transactional
    @PostMapping("/make-appointment")
    public String doMakeAppointment(@ModelAttribute("appointment") Appointment appointment,
                                    HttpSession session, Model model,
                                    RedirectAttributes redirectAttributes) {
//        User currentUser = (User) session.getAttribute("user");
        Patient p = new Patient();
        p.setPatientId(11);


        // check span in the same day
        if (!appointmentService.canBookAppointment(p.getPatientId())) {
            redirectAttributes.addFlashAttribute("message", "❌ You already limit book an appointment. \nPlease choose another day.");
            return "redirect:/appointment/appointment-list";
        }

        appointment.setPatient(p);

        appointment.setStatus(AppointmentStatus.PENDING);
        Appointment isCreateAppointment = appointmentService.saveAppointment(appointment);

        if (isCreateAppointment != null) {
            System.out.println("success");
            redirectAttributes.addFlashAttribute("message", "Create Appointment Successfully");
        } else {
            System.out.println("fail");
            redirectAttributes.addFlashAttribute("message", "Failed to create Appointment Successfully");
            return "redirect:/appointment/appointment-list";
        }

        return "redirect:/appointment/appointment-list";
    }

    @GetMapping("/cancel/{id}")
    public String cancelAppointment(@PathVariable(name = "id") int id, Model model) {
        Appointment appointment = appointmentService.findAppointmentById(id);

        model.addAttribute("appointment", appointment);
        return "patient/cancel-appointment";
    }

    @Transactional
    @PostMapping("/cancel")
    public String cancelAppointment(@ModelAttribute(name = "appointment") Appointment cancelAppointment) {
        Appointment existAppointment = appointmentService.findAppointmentById(cancelAppointment.getAppointmentId());
        if (existAppointment == null) {
            return "redirect:/appointment/appointment-list";
        }

        // update status cancel
        existAppointment.setStatus(AppointmentStatus.CANCELLED);
        existAppointment.setCancelReason(cancelAppointment.getCancelReason());

        appointmentService.saveAppointment(existAppointment);
        return "redirect:/appointment/appointment-list";
    }


    @GetMapping("/edit/{id}")
    public String editAppointment(@PathVariable(name = "id") int id, Model model) {
        Appointment appointment = appointmentService.findAppointmentById(id);
        model.addAttribute("appointment", appointment);
        return "/patient/edit-appointment";
    }

    @Transactional
    @PostMapping("/update")
    public String updateAppointment(@ModelAttribute(name = "appointment") Appointment appointment) {
        Appointment existing = appointmentService.findAppointmentById(appointment.getAppointmentId());
        if (existing == null) {
            return "redirect:/appointment/appointment-list";
        }

        existing.setAppointmentDate(appointment.getAppointmentDate());
        existing.setNotes(appointment.getNotes());

        appointmentService.saveAppointment(existing);
        return "redirect:/appointment/appointment-list";
    }

}