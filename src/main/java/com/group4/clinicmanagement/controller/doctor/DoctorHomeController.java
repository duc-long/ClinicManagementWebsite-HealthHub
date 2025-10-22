package com.group4.clinicmanagement.controller.doctor;

import com.group4.clinicmanagement.entity.Department;
import com.group4.clinicmanagement.entity.Doctor;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.security.CustomUserDetails;
import com.group4.clinicmanagement.service.DoctorService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DoctorHomeController {
    private DoctorService doctorService;

    public DoctorHomeController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @RequestMapping({"/doctor/home"})
    public String home(Model model,
                       Authentication authentication) {
        String username = authentication.getName();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser(); // hoặc getUserId() nếu bạn có method đó

        Doctor doctor = doctorService.findDoctorById(user.getUserId());
        Department department = doctorService.findDoctorDepartment(doctor.getDepartment().getDepartmentId());

        model.addAttribute("username", username);
        model.addAttribute("department", department.getName());
        return "doctor/home";
    }
}
