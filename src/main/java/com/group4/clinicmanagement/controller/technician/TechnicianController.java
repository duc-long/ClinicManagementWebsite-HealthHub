package com.group4.clinicmanagement.controller.technician;

import com.group4.clinicmanagement.dto.LabRequestDTO;
import com.group4.clinicmanagement.dto.LabResultDTO;
import com.group4.clinicmanagement.dto.UserDTO;
import com.group4.clinicmanagement.entity.LabRequest;
import com.group4.clinicmanagement.entity.LabResult;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.enums.LabRequestStatus;
import com.group4.clinicmanagement.service.LabRequestService;
import com.group4.clinicmanagement.service.LabResultService;
import com.group4.clinicmanagement.service.TechnicianService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/technician")
public class TechnicianController {
    private final int id = 18;
    TechnicianService technicianService;
    LabRequestService labRequestService;
    LabResultService labResultService;

    public TechnicianController(TechnicianService technicianService, LabRequestService labRequestService, LabResultService labResultService) {
        this.technicianService = technicianService;
        this.labRequestService = labRequestService;
        this.labResultService = labResultService;
    }

    @GetMapping(value = "/login")
    public String login() {
        return "auth/technician/login";
    }

    @GetMapping(value = "/dashboard")
    public String technicianHome(Model model) {
        List<LabResultDTO> labResultDTOS = new ArrayList<>(labResultService.findLabResultList());
        labResultDTOS.removeIf(r -> !"PAID".equals(r.getLabRequestStatus()));

        List<LabRequestDTO> labRequestDTOS = new ArrayList<>(labRequestService.getAllLabRequestDTO());
        labRequestDTOS.removeIf(r -> !r.getStatus().equals( LabRequestStatus.REQUESTED));

        model.addAttribute("labRequestDTOS", labRequestDTOS);
        model.addAttribute("labResultDTOS", labResultDTOS);

        return "technician/dashboard";
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
    public String editProfile(@ModelAttribute("userDTO") UserDTO userDTO) {
        User user = technicianService.findByUserId(id);

        user.setFullName(userDTO.getFullName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setGender(userDTO.getGender());

        technicianService.save(user);
        return "redirect:/technician/profile";
    }

}
