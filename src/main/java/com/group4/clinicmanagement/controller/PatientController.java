package com.group4.clinicmanagement.controller;

import com.group4.clinicmanagement.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/patient")
public class PatientController {
    @Autowired
    private PatientService patientService;

//    @GetMapping()
////    @ResponseBody
//    public List<PatientUserDTO> getAllPatients() {
//        patientService.getAllPatients();
//        return "";
//    }

    @GetMapping("/profile/{username}")
    public String getPatientsByUsername(Model model, @PathVariable("username") String username) {
        model.addAttribute("patient", patientService.getPatientsByUsername(username).get());
        patientService.getPatientsByUsername(username).stream().toList();
        return "patient/profile";
    }

//    @PutMapping("/edit-profile")
////    @ResponseBody
//    public ResponseEntity<PatientUserDTO> updateProfile(@RequestBody PatientUserDTO dto, HttpSession session) {
//        String username = (String) session.getAttribute("username");
//        if (username == null) {
//            return ResponseEntity.status(401).body(null); // Unauthorized nếu không có username trong session
//        }
//        try {
//            PatientUserDTO updatedProfile = patientService.savePatientUser(username, dto);
//            return ResponseEntity.ok(updatedProfile); // trả về profile mới sau khi update
//        } catch (RuntimeException ex) {
//            return ResponseEntity.badRequest().body(null);
//        }
//    }
}
