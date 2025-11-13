package com.group4.clinicmanagement.controller.appointment;


import com.group4.clinicmanagement.entity.Appointment;
import com.group4.clinicmanagement.entity.Patient;
import com.group4.clinicmanagement.enums.AppointmentStatus;
import com.group4.clinicmanagement.repository.PatientRepository;
import com.group4.clinicmanagement.service.AppointmentService;
import com.group4.clinicmanagement.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/patient/appointment")
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final PatientRepository patientRepository;

    // constructor
    public AppointmentController(AppointmentService appointmentService, PatientRepository patientRepository, UserService userService) {
        this.appointmentService = appointmentService;
        this.patientRepository = patientRepository;
    }

    // method show the main view appointment
    @RequestMapping("/manage")
    public String appointmentPage(Model model, Principal principal) {
        Patient currentUser = patientRepository.findPatientByUsername(principal.getName()).orElse(null);

        if (currentUser == null) {
            System.out.println("cannot find user");
            return "redirect:/login";
        }

        List<Appointment> all = appointmentService.findAllByPatientId(currentUser.getPatientId());
        List<Appointment> active = all.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.CHECKED_IN)
                .toList();

        System.out.println("manage start");
        for (Appointment a : all) {
            System.out.println(a.getAppointmentId() + " " + a.getStatus());
        }

        model.addAttribute("current", active);
        return "patient/appointment";
    }

    // method to filter the view appointment
    @GetMapping("/view")
    public String filterAppointments(@RequestParam String type, Model model, Principal principal) {
        Patient currentUser = patientRepository.findPatientByUsername(principal.getName()).orElse(null);
        if (currentUser == null) return "redirect:/patient/login";

        List<Appointment> all = appointmentService.findAllByPatientId(currentUser.getPatientId());
        List<Appointment> filterd;

        // view classification
        switch (type) {
            case "history": // history view
                filterd = all.stream().filter(a -> a.getStatus() == AppointmentStatus.PAID).toList();
                model.addAttribute("history", filterd);
                return "fragment/patient/appointment-cards :: history";
            case "submitted": // submitted view
                filterd = all.stream().filter(a ->
                        a.getStatus() == AppointmentStatus.PENDING ||
                                a.getStatus() == AppointmentStatus.CONFIRMED).toList();
                model.addAttribute("submitted", filterd);
                return "fragment/patient/appointment-cards :: submitted";
            case "cancelled": // cancelled view
                filterd = all.stream().filter(a -> a.getStatus() == AppointmentStatus.CANCELLED).toList();
                model.addAttribute("cancelled", filterd);
                return "fragment/patient/appointment-cards :: cancelled";
            case "current": // current vỉew
                filterd = all.stream().filter(a -> a.getStatus() == AppointmentStatus.CHECKED_IN).toList();
                model.addAttribute("current", filterd);
                return "fragment/patient/appointment-cards :: current";
            case "examined":
                filterd = all.stream().filter(a -> a.getStatus() == AppointmentStatus.EXAMINED).toList();
                model.addAttribute("examined", filterd);
                return "fragment/patient/appointment-cards :: examined";
            default:
                filterd = all;
                break;
        }

        model.addAttribute("appointments", filterd);
        return "fragment/patient/appointment-cards :: appointmentListFragment";
    }

    // method to show appointment detail
    @GetMapping("/detail/{id}")
    public String viewAppointmentDetail(@PathVariable("id") String idStr, Model model, RedirectAttributes redirect, Principal principal) {
        try {
            // Kiểm tra người dùng đã đăng nhập chưa
            Patient currentUser = patientRepository.findPatientByUsername(principal.getName()).orElse(null);
            if (currentUser == null) {
                return "redirect:/login";
            }

            // Kiểm tra xem id có phải là số hợp lệ hay không
            int id = Integer.parseInt(idStr);

            // Tìm lịch hẹn theo id
            Appointment appointment = appointmentService.findAppointmentById(id);
            if (appointment == null || appointment.getPatient().getPatientId() != currentUser.getPatientId()) {
                return "redirect:/patient/appointment/manage";
            }

            // Thêm thông tin lịch hẹn vào model
            model.addAttribute("appointment", appointment);
            return "patient/appointment-detail";

        } catch (NumberFormatException e) {
            // Nếu id không phải là một số hợp lệ, chuyển hướng về trang danh sách lịch hẹn với thông báo lỗi
            redirect.addFlashAttribute("error", "Invalid appointment ID format.");
            return "redirect:/patient/appointment/manage";
        } catch (Exception e) {
            // Bắt lỗi chung nếu có lỗi khác xảy ra
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/patient/appointment/manage";
        }
    }

    @GetMapping("/make-appointment")
    public String makeAppointment(Model model) {
        model.addAttribute("appointment", new Appointment());
        return "/patient/make-appointment";
    }

    // method to create an appointment
    @Transactional
    @PostMapping("/make-appointment")
    public String doMakeAppointment(@ModelAttribute("appointment") Appointment appointment,
                                    Principal principal,
                                    RedirectAttributes redirectAttributes) {
        Patient user = patientRepository.findPatientByUsername(principal.getName()).orElse(null);
        Patient patient = patientRepository.findById(user.getPatientId()).orElse(null);

        // check span in the same day
        if (!appointmentService.canBookAppointment(patient.getPatientId())) {
            redirectAttributes.addFlashAttribute("message", "❌ You already limit book an appointment. \nPlease choose another day.");
            redirectAttributes.addFlashAttribute("messageType","error");
            System.out.println("You already limit book an appointment ");
            return "redirect:/patient/appointment/make-appointment";
        }

        // check valid booking appointment date
        if (!appointmentService.isBookAppointmentValidDate(appointment.getAppointmentDate())) {
            redirectAttributes.addFlashAttribute("message", "Invalid booking date, You can only book an appointment after 2 days.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/patient/appointment/make-appointment";
        }

        appointment.setPatient(patient);
        appointment.setStatus(AppointmentStatus.PENDING);
        Appointment isCreateAppointment = appointmentService.saveAppointment(appointment);

        // Notification
        if (isCreateAppointment != null) { // if success
            System.out.println("success");
            redirectAttributes.addFlashAttribute("message", "Create Appointment Successfully");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } else {
            System.out.println("fail"); // if fail
            redirectAttributes.addFlashAttribute("message", "Failed to create Appointment Successfully");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/patient/appointment/manage";
        }

        System.out.println("update appointment successfully");
        return "redirect:/patient/appointment/manage";
    }

    // method cancel redirect to cancel appointment page
    @GetMapping("/cancel/{id}")
    public String cancelAppointment(@PathVariable(name = "id") int id, Model model) {
        Appointment appointment = appointmentService.findAppointmentById(id);

        model.addAttribute("appointment", appointment);
        return "patient/cancel-appointment";
    }

    // method do cancel appointment
    @Transactional
    @PostMapping("/cancel")
    public String cancelAppointment(@ModelAttribute(name = "appointment") Appointment cancelAppointment,
                                    RedirectAttributes redirectAttributes) {
        Appointment existAppointment = appointmentService.findAppointmentById(cancelAppointment.getAppointmentId());
        if (existAppointment == null) {
            redirectAttributes.addFlashAttribute("message", "Cancel appointment failed");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/patient/appointment/manage";
        }

        // update status cancel
        existAppointment.setStatus(AppointmentStatus.CANCELLED);
        existAppointment.setCancelReason(cancelAppointment.getCancelReason());

        // update cancel status to DB
        appointmentService.saveAppointment(existAppointment);
        redirectAttributes.addFlashAttribute("message", "Cancel appointment successfully");
        redirectAttributes.addFlashAttribute("messageType", "success");

        System.out.println("cancel appointment successfully");
        return "redirect:/patient/appointment/manage";
    }

    // method redirect to edit page
    @GetMapping("/edit/{id}")
    public String editAppointment(@PathVariable(name = "id") int id, Model model,
                                  RedirectAttributes redirectAttributes) {
        Appointment appointment = appointmentService.findAppointmentById(id);

        // check valid appointment
        if (appointment == null || appointment.getAppointmentId() != id) {
            System.out.println("edit appointment failed");
            redirectAttributes.addFlashAttribute("message", "Appointment does not exist");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/patient/appointment/manage";
        }

        model.addAttribute("appointment", appointment);
        return "/patient/edit-appointment";
    }

    // method to update appointment information
    @Transactional
    @PostMapping("/update")
    public String updateAppointment(@ModelAttribute(name = "appointment") Appointment appointment,
                                    RedirectAttributes redirectAttributes) {
        Appointment existing = appointmentService.findAppointmentById(appointment.getAppointmentId());
        if (existing == null) {
            redirectAttributes.addFlashAttribute("message", "Appointment does not exist");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/patient/appointment/manage";
        }

        // set new information
        existing.setAppointmentDate(appointment.getAppointmentDate());
        existing.setNotes(appointment.getNotes());

        // update new information
        appointmentService.saveAppointment(existing);

        redirectAttributes.addFlashAttribute("message", "Appointment updated successfully");
        redirectAttributes.addFlashAttribute("messageType", "success");

        System.out.println("update appointment successfully");
        return "redirect:/patient/appointment/manage";
    }

}