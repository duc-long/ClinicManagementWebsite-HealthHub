package com.group4.clinicmanagement.controller.doctor;

import com.group4.clinicmanagement.dto.AppointmentDTO;
import com.group4.clinicmanagement.dto.DoctorDTO;
import com.group4.clinicmanagement.entity.Appointment;
import com.group4.clinicmanagement.entity.Department;
import com.group4.clinicmanagement.entity.Doctor;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.enums.AppointmentStatus;
import com.group4.clinicmanagement.repository.AppointmentRepository;
import com.group4.clinicmanagement.repository.DepartmentRepository;
import com.group4.clinicmanagement.security.CustomUserDetails;
import com.group4.clinicmanagement.service.AppointmentService;
import com.group4.clinicmanagement.service.DoctorService;
import com.group4.clinicmanagement.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/doctor")
public class DoctorController {
    private final DoctorService doctorService;
    private final UserService userService;
    private final DepartmentRepository departmentRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentService appointmentService;

    public DoctorController(DoctorService doctorService, UserService userService, DepartmentRepository departmentRepository,
                            AppointmentRepository appointmentRepository, AppointmentService appointmentService) {
        this.doctorService = doctorService;
        this.userService = userService;
        this.departmentRepository = departmentRepository;
        this.appointmentRepository = appointmentRepository;
        this.appointmentService = appointmentService;
    }

    @GetMapping({"/overview"})
    public String home(Model model,
                       Authentication authentication) {
        String username = authentication.getName();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser(); // hoặc getUserId() nếu bạn có method đó

        Doctor doctor = doctorService.findDoctorById(user.getUserId());
        Department department = doctorService.findDoctorDepartment(doctor.getDepartment().getDepartmentId());

        model.addAttribute("username", username);
        model.addAttribute("department", department.getName());
        model.addAttribute("section", "overview");
        return "doctor/home";
    }

    @GetMapping("/view/{section}")
    public String loadFragment(@PathVariable String section, Principal principal, Model model) {
        User user = userService.findUserByUsername(principal.getName());
        if (user == null) return "redirect:/doctor/login";

        Doctor doctor = doctorService.findDoctorById(user.getUserId());
        model.addAttribute("doctor", doctor);

        switch (section) {
            case "profile":
                DoctorDTO doctorDTO = new DoctorDTO();
                doctorDTO.setGender(user.getGender());
                doctorDTO.setEmail(user.getEmail());
                doctorDTO.setPhone(user.getPhone());
                doctorDTO.setFullName(user.getFullName());
                doctorDTO.setDoctorId(user.getUserId());
                doctorDTO.setAvatarFileName(user.getAvatar());
                doctorDTO.setUsername(user.getUsername());
                Department department = departmentRepository.findByDepartmentId(doctor.getDepartment().getDepartmentId())
                        .orElse(null);
                model.addAttribute("doctor", doctorDTO);
                model.addAttribute("department", department.getName());
                return "fragment/doctor/doctor-fragment :: profile";
            case "overview":
                return "fragment/doctor/doctor-fragment :: overview";
            case "appointments":
                try {
                    List<AppointmentDTO> appointmentList = appointmentRepository
                            .findByDoctor_DoctorIdAndStatusValue(user.getUserId(), AppointmentStatus.CHECKED_IN.getValue())
                            .stream()
                            .map(a -> new AppointmentDTO(
                                    a.getAppointmentId(),
                                    a.getDoctor() != null && a.getDoctor().getUser() != null ? a.getDoctor().getUser().getFullName() : "Unknown",
                                    a.getPatient() != null && a.getPatient().getUser() != null ? a.getPatient().getUser().getFullName() : "Unknown",
                                    a.getReceptionist() != null ? a.getReceptionist().getFullName() : "Unknown",
                                    a.getAppointmentDate(),
                                    a.getCreatedAt(),
                                    a.getStatus(),
                                    a.getQueueNumber(),
                                    a.getNotes(),
                                    a.getCancelReason()
                            ))
                            .toList();
                    System.out.println("Lấy ssc");
                    model.addAttribute("appointments", appointmentList);
                    return "fragment/doctor/doctor-fragment :: appointments";
                } catch (Exception e) {
                    System.out.println("Lỗi excepetion");
                    e.printStackTrace();
                    model.addAttribute("appointments", List.of());
                    model.addAttribute("error", e.getMessage());
                    return "fragment/doctor/doctor-fragment :: appointments";
                }
            default:
                return "fragment/doctor/doctor-fragment :: overview";
        }
    }

    @GetMapping("/profile")
    public String loadProfile(Principal principal, Model model) {
        User user = userService.findUserByUsername(principal.getName());
        if (user == null) return "redirect:/doctor/login";

        Doctor doctor = doctorService.findDoctorById(user.getUserId());
        model.addAttribute("doctor", doctor);

        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setGender(user.getGender());
        doctorDTO.setEmail(user.getEmail());
        doctorDTO.setPhone(user.getPhone());
        doctorDTO.setFullName(user.getFullName());
        doctorDTO.setDoctorId(user.getUserId());
        doctorDTO.setAvatarFileName(user.getAvatar());
        doctorDTO.setUsername(user.getUsername());
        Department department = departmentRepository.findByDepartmentId(doctor.getDepartment().getDepartmentId())
                .orElse(null);
        model.addAttribute("doctor", doctorDTO);
        model.addAttribute("department", department.getName());
        model.addAttribute("section", "profile");
        return "doctor/home";
    }

    @GetMapping("/appointment/detail/{id}")
    public String loadAppointmentDetail(@PathVariable int id, Model model,
                                        RedirectAttributes redirectAttributes,
                                        Principal principal) {
        Appointment a = appointmentService.findById(id);
        User user = userService.findUserByUsername(principal.getName());

        if (user.getUserId() != a.getDoctor().getDoctorId()) {
            redirectAttributes.addFlashAttribute("message", "You can't access this appointment");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/doctor/appointment/detail/" + id;
        }

        AppointmentDTO dto = new AppointmentDTO(
                a.getAppointmentId(),
                a.getDoctor().getUser().getFullName(),
                a.getPatient().getUser().getFullName(),
                a.getReceptionist() != null ? a.getReceptionist().getFullName() : "N/A",
                a.getAppointmentDate(),
                a.getCreatedAt(),
                a.getStatus(),
                a.getQueueNumber(),
                a.getNotes(),
                a.getCancelReason()
        );

        model.addAttribute("appointment", dto);
        return "fragment/doctor/doctor-fragment :: appointment-detail";
    }

    // method to redirect to update profile page
    @GetMapping("/profile/edit/{id}")
    public String updateProfile(Model model,
                                @PathVariable(name = "id") int id,
                                Principal principal) {
        User user = userService.findUserByUsername(principal.getName());

        // check valid user info
        if (user.getUserId() != id) {
            return "redirect:/doctor/login";
        }

        Doctor doctor = doctorService.findDoctorById(id);
        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setGender(user.getGender());
        doctorDTO.setEmail(user.getEmail());
        doctorDTO.setPhone(user.getPhone());
        doctorDTO.setFullName(user.getFullName());
        doctorDTO.setDoctorId(user.getUserId());
        doctorDTO.setAvatarFileName(user.getAvatar());
        doctorDTO.setUsername(user.getUsername());
        doctorDTO.setBio(doctor.getBio());

        model.addAttribute("doctor", doctorDTO);
        return "doctor/edit-profile";
    }

    // method to update doctor profile
    @PostMapping("/profile/update")
    public String doUpdateProfile(Model model,
                                  Principal principal,
                                  @ModelAttribute("doctor") DoctorDTO doctorModel,
                                  RedirectAttributes redirectAttributes) {

        User user = userService.findUserByUsername(principal.getName());
        if (user == null) return "redirect:/login";

        Doctor doctor = doctorService.findDoctorById(user.getUserId());
        if (doctor == null) return "redirect:/doctor/overview";

        // --- Update user basic info ---
        user.setFullName(doctorModel.getFullName());
        user.setUsername(doctorModel.getUsername());
        user.setEmail(doctorModel.getEmail());
        user.setPhone(doctorModel.getPhone());
        user.setGender(doctorModel.getGender());
        userService.saveUser(user);

        // --- Update doctor-specific info ---
        doctor.setBio(doctorModel.getBio());
        doctorService.saveDoctor(doctor);

        redirectAttributes.addFlashAttribute("message", "Doctor profile updated successfully!");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/doctor/overview";
    }


}
