package com.group4.clinicmanagement.controller.doctor;

import com.group4.clinicmanagement.dto.UserDTO;
import com.group4.clinicmanagement.entity.Patient;
import com.group4.clinicmanagement.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
//@RequestMapping("/patient/profile")
public class ProfileController {

    @GetMapping("/view")
    public String viewProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Patient userEntity = userDetails.getPatient();

        if (userEntity == null) {
            return "redirect:/login?error=true";
        }

        UserDTO dto = new UserDTO();
        dto.setId(userEntity.getPatientId());
        dto.setUsername(userEntity.getUsername());
        dto.setEmail(userEntity.getEmail());
        dto.setPhone(userEntity.getPhone());
        dto.setGender(userEntity.getGender());
        dto.setFullName(userEntity.getFullName());

        // Truyền vào model để Thymeleaf hiển thị
        model.addAttribute("user", dto);
        return "user/profile"; // tương ứng với templates/user/profile.html
    }
}