package com.group4.clinicmanagement.controller;

import com.group4.clinicmanagement.dto.UserDTO;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.service.TechnicianService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/technician")
public class TechnicianController {
    private final int id = 18;
    TechnicianService technicianService;

    public TechnicianController(TechnicianService technicianService) {
        this.technicianService = technicianService;
    }

    @GetMapping
    public String technicianHome(Model model) {

        return "technician/technician-dashboard";
    }

    @GetMapping(value = "/profile")
    public String viewProfile(Model model) {

        User user = technicianService.findByUserId(id);

        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getUserId());
        userDTO.setFullName(user.getFullName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhone(user.getPhone());
        userDTO.setGender(user.getGender());

        model.addAttribute("userDTO", userDTO);

        return "technician/profile";
    }

    @GetMapping(value = "edit-profile")
    public String editProfile(Model model) {
        User user = technicianService.findByUserId(id);

        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getUserId());
        userDTO.setFullName(user.getFullName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhone(user.getPhone());
        userDTO.setGender(user.getGender());

        model.addAttribute("userDTO", userDTO);

        return "technician/edit-profile";
    }

    @PostMapping(value = "edit-profile")
    public String editProfile( @ModelAttribute("userDTO") UserDTO userDTO) {
        User user = technicianService.findByUserId(id);

        user.setFullName(userDTO.getFullName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setGender(userDTO.getGender());

        technicianService.save(user);
        return "redirect:/technician/profile";
    }
}
