package com.group4.clinicmanagement.controller;

import com.group4.clinicmanagement.dto.UserDTO;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.enums.Gender;
import com.group4.clinicmanagement.service.ReceptionistService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/receptionist")
public class ReceptionistController {

    private final ReceptionistService receptionistService;

    public ReceptionistController(ReceptionistService receptionistService) {
        this.receptionistService = receptionistService;
    }

    // View profile page
    @GetMapping("/profile")
    public String viewReceptionistProfile(Model model) {
        int receptionistId = 4; // Hardcoded for demo
        User user = receptionistService.findUserById(receptionistId);

        UserDTO dto = new UserDTO(
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getGender() != null ? user.getGender().getValue() : 0,
                user.getUserId()
        );

        model.addAttribute("receptionist", dto);
        return "receptionist/profile";
    }

    // Show edit form
    @GetMapping("/edit-profile")
    public String showEditForm(Model model) {
        int receptionistId = 4;
        User user = receptionistService.findUserById(receptionistId);

        UserDTO dto = new UserDTO(
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getGender() != null ? user.getGender().getValue() : 0,
                user.getUserId()
        );

        model.addAttribute("receptionist", dto);
        return "receptionist/edit-profile";
    }

    // Handle profile update
    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("receptionist") UserDTO dto, Model model) {
        User receptionist = receptionistService.findUserById(dto.getId());
        receptionist.setFullName(dto.getFullName());
        receptionist.setEmail(dto.getEmail());
        receptionist.setPhone(dto.getPhone());
        receptionist.setGender(Gender.fromInt(dto.getGender()));

        receptionistService.save(receptionist);

        model.addAttribute("receptionist", dto);
        model.addAttribute("success", "Receptionist profile updated successfully.");
        return "receptionist/profile";
    }
}
