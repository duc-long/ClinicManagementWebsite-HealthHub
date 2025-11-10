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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
            @RequestParam(required = false) String viewAll,
            Model model, RedirectAttributes redirectAttributes) {

        try {
            patientId = (patientId != null && !patientId.isBlank()) ? patientId.trim() : null;
            doctorName = (doctorName != null && !doctorName.isBlank()) ? doctorName.trim() : null;
            testName = (testName != null && !testName.isBlank()) ? testName.trim() : null;
            status = (status != null && !status.isBlank()) ? status.trim() : null;
            boolean isAll = "true".equalsIgnoreCase(viewAll);

            if (viewAll != null && !"true".equalsIgnoreCase(viewAll) && !"false".equalsIgnoreCase(viewAll)) {
                isAll= true;
            }

            List<LabRequestDTO> labRequestDTOs;

            if (patientId == null && status == null && doctorName == null && testName == null && isAll) {
                labRequestDTOs = labRequestService.getAllLabRequestDTO();
            } else {
                labRequestDTOs = labRequestService.filterRequests(patientId, doctorName, testName, status, isAll);
            }

            List<LabTestCatalog> labTestCatalogs = labTestCatalogService.getAll();
            List<Doctor> doctors = doctorService.findAllDoctors();

            LocalDate now = LocalDate.now();


            model.addAttribute("labTestCatalogs", labTestCatalogs);
            model.addAttribute("labRequests", labRequestDTOs);
            model.addAttribute("doctors", doctors);


            model.addAttribute("patientId", patientId);
            model.addAttribute("doctorName", doctorName);
            model.addAttribute("testName", testName);
            model.addAttribute("status", status);
            model.addAttribute("viewAll", isAll);
            model.addAttribute("date", now);


            return "technician/request-list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Something wrong");
            return "redirect:/technician/result-list";
        }
    }

    @GetMapping(value = "/request/{id}")
    public String requestDetail(@PathVariable(name = "id") String id,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        try {
            int labRequestId = Integer.parseInt(id);
            LabRequest labRequest = labRequestService.findLabRequestById(labRequestId);

            if (labRequest == null) {
                model.addAttribute("mess", "Lab request not found");
                return "technician/request-detail";
            }

            model.addAttribute("request", labRequest);
            return "technician/request-detail";

        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid request id");
            return "redirect:/technician/request-list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Something wrong");
            return "redirect:/technician/request-list";
        }
    }
}
