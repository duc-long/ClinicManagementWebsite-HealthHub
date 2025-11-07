package com.group4.clinicmanagement.controller.receptionist;

import com.group4.clinicmanagement.dto.ReceptionistAppointmentDTO;
import com.group4.clinicmanagement.dto.ReceptionistUserDTO;
import com.group4.clinicmanagement.dto.UserDTO;
import com.group4.clinicmanagement.entity.Appointment;
import com.group4.clinicmanagement.entity.Department;
import com.group4.clinicmanagement.entity.Doctor;
import com.group4.clinicmanagement.enums.AppointmentStatus;
import com.group4.clinicmanagement.service.DepartmentService;
import com.group4.clinicmanagement.service.ReceptionistService;
import com.group4.clinicmanagement.service.AppointmentService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/receptionist")
public class ReceptionistController {

    private final ReceptionistService receptionistService;
    private final AppointmentService appointmentService;
    private final DepartmentService departmentService;

    public ReceptionistController(ReceptionistService receptionistService, AppointmentService appointmentService, DepartmentService departmentService) {
        this.receptionistService = receptionistService;
        this.appointmentService = appointmentService;
        this.departmentService = departmentService;
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
    public String listAppointments(@RequestParam(required = false, defaultValue = "ALL") String status,
                                   @RequestParam(defaultValue = "1") String page,
            @RequestParam(defaultValue = "10") int size,Model model) {
        Page<ReceptionistAppointmentDTO> appointments;
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

    @GetMapping("/appointment/{id}")
    public String viewAppointmentDetails(@PathVariable("id") Integer id, Model model) {
        Appointment appointment = appointmentService.getAppointmentForReceptionist(id);
        model.addAttribute("appointment", appointment);
        return "receptionist/appointment-details";
    }

    @GetMapping("/appointment/schedule/{id}")
    public String showEditAppointmentForm(@PathVariable("id") Integer id,
            @RequestParam(value = "departmentId", required = false) Integer departmentId,Model model) {
        Appointment appointment = appointmentService.getById(id);
       List<Department> departments = departmentService.getAllDepartment();
       List<Doctor> doctorList = new ArrayList<>();
       if(departments!=null){
           doctorList = appointmentService.getAvailableDoctors(departmentId, appointment.getAppointmentDate());
       }
        model.addAttribute("appointment", appointment);
       model.addAttribute("department", departments);
        model.addAttribute("selectedDepartmentId", departmentId);
        model.addAttribute("availableDoctors", doctorList);
        model.addAttribute("statuses", AppointmentStatus.values());
        return "receptionist/appointment-edit";
    }

    @PostMapping("/appointment/schedule")
    public String updateAppointment(@ModelAttribute("appointment") Appointment updatedAppointment) {
        if (updatedAppointment.getStatusValue() != null) {
            updatedAppointment.setStatus(AppointmentStatus.fromInt(updatedAppointment.getStatusValue()));
        }
        appointmentService.scheduleAppointment(updatedAppointment);
        return "redirect:/receptionist/appointment-list";
    }
}

