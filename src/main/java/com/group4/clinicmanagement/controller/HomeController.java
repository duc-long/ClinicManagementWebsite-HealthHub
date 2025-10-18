package com.group4.clinicmanagement.controller;

import com.group4.clinicmanagement.dto.DepartmentDTO;
import com.group4.clinicmanagement.dto.DoctorUserDTO;
import com.group4.clinicmanagement.service.DepartmentService;
import com.group4.clinicmanagement.service.DoctorService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping(value = "/home")
public class HomeController {

    private final DoctorService doctorService;
    private final DepartmentService departmentService;

    public HomeController(DoctorService doctorService, DepartmentService departmentService) {
        this.doctorService = doctorService;
        this.departmentService = departmentService;
    }

    @GetMapping
    public String guestHome(Model model) {
        List<DoctorUserDTO> doctorUserDTOS = doctorService.findTopDoctors(PageRequest.of(0, 4));
        model.addAttribute("doctors", doctorUserDTOS);
        List<DepartmentDTO> departments = departmentService.findAll();
        model.addAttribute("departments", departments);
        return "home/HomeGuest";
    }

    @GetMapping(value = "/list-doctor")
    public String listDoctor(Model model) {
        List<DoctorUserDTO> doctorUserDTOS = doctorService.findAllVisibleAndActiveDoctorsDoctorUserDTOS();
        model.addAttribute("doctors", doctorUserDTOS);
        List<DepartmentDTO> departments = departmentService.findAll();
        model.addAttribute("departments", departments);
        return "home/doctor-list";
    }

    @GetMapping(value = "/search-doctor")
    public String searchDoctor(Model model) {
        model.addAttribute("doctors", null);
        List<DepartmentDTO> departments = departmentService.findAll();
        model.addAttribute("departments", departments);
        return "home/doctor-search";
    }

    @GetMapping(value = "/search-doctor-result")
    public String searchDoctor(@RequestParam(name = "departmentId") Integer departmentId,
                               @RequestParam(name = "doctorName") String doctorName,
                               Model model) {
        List<DoctorUserDTO> doctorUserDTOS = doctorService.findByNameContainingIgnoreCaseAndDepartmentId(doctorName, departmentId);
        model.addAttribute("doctors", doctorUserDTOS);
        List<DepartmentDTO> departments = departmentService.findAll();
        model.addAttribute("departments", departments);
        model.addAttribute("pageTitle", "Search Results" + "(" + doctorUserDTOS.size() + " doctors found)");
        return "home/doctor-search";
    }

    @GetMapping(value = "/doctor-profile/{doctorId}")
    public String viewDetailDoctor(Model model, @PathVariable(name = "doctorId") int doctorId) {
        DoctorUserDTO doctorUserDTO = doctorService.findVisibleActiveDoctorById(doctorId);
        model.addAttribute("doctor", doctorUserDTO);
        List<DepartmentDTO> departments = departmentService.findAll();
        model.addAttribute("departments", departments);
        return "home/doctor-profile";
    }

    @GetMapping(value = "/department/{departmentName}")
    public String viewDoctorSpecialty(Model model, @PathVariable(name = "departmentName") String departmentName) {
        List<DoctorUserDTO> doctors = doctorService.findVisibleActiveDoctorsByDepartment(departmentName);
        model.addAttribute("doctors", doctors);
        List<DepartmentDTO> departments = departmentService.findAll();
        model.addAttribute("departments", departments);
        model.addAttribute("departmentName", departmentName);
        return "home/department-doctor-list";
    }

}
