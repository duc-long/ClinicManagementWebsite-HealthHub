package com.group4.clinicmanagement.controller.receptionist;

import com.group4.clinicmanagement.dto.UserDTO;
import com.group4.clinicmanagement.service.ReceptionistService;
import com.group4.clinicmanagement.service.AppointmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        int receptionistId = 4; // Demo data
        UserDTO dto = receptionistService.getReceptionistProfile(receptionistId);
        model.addAttribute("receptionist", dto);
        return "receptionist/profile";
    }

    @GetMapping("/edit-profile")
    public String editProfile(Model model) {
        int receptionistId = 4;
        UserDTO dto = receptionistService.getReceptionistProfile(receptionistId);
        model.addAttribute("receptionist", dto);
        return "receptionist/edit-profile";
    }

    @PostMapping("/edit-profile")
    public String editProfile(@ModelAttribute("receptionist") UserDTO dto) {
        receptionistService.updateReceptionistProfile(dto);
        return "redirect:/receptionist/profile";
    }
}
