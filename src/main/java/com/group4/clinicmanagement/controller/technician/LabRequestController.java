package com.group4.clinicmanagement.controller.technician;

import com.group4.clinicmanagement.dto.LabRequestDTO;
import com.group4.clinicmanagement.entity.Doctor;
import com.group4.clinicmanagement.entity.LabRequest;
import com.group4.clinicmanagement.entity.LabTestCatalog;
import com.group4.clinicmanagement.service.DoctorService;
import com.group4.clinicmanagement.service.LabRequestService;
import com.group4.clinicmanagement.service.LabTestCatalogService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping(value = "/technician")
public class LabRequestController {
    LabRequestService labRequestService;
    LabTestCatalogService labTestCatalogService;
    DoctorService doctorService;

    public LabRequestController(LabRequestService labRequestService, LabTestCatalogService labTestCatalogService, DoctorService doctorService) {
        this.labRequestService = labRequestService;
        this.labTestCatalogService = labTestCatalogService;
        this.doctorService = doctorService;
    }

    @GetMapping("/request-list")
    public String requestList(
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) String doctorName,
            @RequestParam(required = false) String testName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Model model) {

        patientId = (patientId != null && !patientId.isBlank()) ? patientId.trim() : null;
        doctorName = (doctorName != null && !doctorName.isBlank()) ? doctorName.trim() : null;
        testName = (testName != null && !testName.isBlank()) ? testName.trim() : null;
        status = (status != null && !status.isBlank()) ? status.trim() : null;

        List<LabRequestDTO> labRequestDTOs;

        if (patientId == null && status == null && doctorName == null && testName == null && fromDate == null && toDate == null) {
            labRequestDTOs = labRequestService.getAllLabRequestDTO();
        } else {
            LocalDateTime from = (fromDate != null) ? fromDate.atStartOfDay() : null;
            LocalDateTime to = (toDate != null) ? toDate.atTime(23, 59, 59) : null;

            labRequestDTOs = labRequestService.filterRequests(patientId, doctorName,testName, status, from, to);
        }

        List<LabTestCatalog> labTestCatalogs = labTestCatalogService.getAll();
        List<Doctor> doctors = doctorService.findAllDoctors();


        model.addAttribute("labTestCatalogs", labTestCatalogs);
        model.addAttribute("labRequests", labRequestDTOs);
        model.addAttribute("doctors", doctors);


        model.addAttribute("patientId", patientId);
        model.addAttribute("doctorName", doctorName);
        model.addAttribute("testName", testName);
        model.addAttribute("status", status);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);

        return "technician/request-list";
    }

    @GetMapping(value = "/request/{id}")
    public String requestDetail(@PathVariable(name = "id") int id, Model model){
        model.addAttribute("request", labRequestService.findLabRequestById(id));
        return "technician/request-detail";
    }

}
