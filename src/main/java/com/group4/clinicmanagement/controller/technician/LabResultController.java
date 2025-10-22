package com.group4.clinicmanagement.controller.technician;

import com.group4.clinicmanagement.entity.LabResult;
import com.group4.clinicmanagement.entity.LabTestCatalog;
import com.group4.clinicmanagement.service.LabRequestService;
import com.group4.clinicmanagement.service.LabResultService;
import com.group4.clinicmanagement.service.LabTestCatalogService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping(value = "/technician")
public class LabResultController {
    LabResultService labResultService;
    LabRequestService labRequestService;
    LabTestCatalogService labTestCatalogService;

    public LabResultController(LabResultService labResultService, LabRequestService labRequestService, LabTestCatalogService labTestCatalogService) {
        this.labResultService = labResultService;
        this.labRequestService = labRequestService;
        this.labTestCatalogService = labTestCatalogService;
    }

    @GetMapping("/result-list")
    public String testList(
            @RequestParam(required = false) String resultId,
            @RequestParam(required = false) String testName,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {

        resultId = (resultId != null && !resultId.isBlank()) ? resultId.trim() : null;
        testName = (testName != null && !testName.isBlank()) ? testName.trim() : null;

        List<LabResult> labResults;

        if (resultId == null && testName == null && date == null) {
            labResults = labResultService.findLabResultList();
        } else {
            labResults = labResultService.filterResults(resultId, testName, date);
        }

        List<LabTestCatalog> labTestCatalogs = labTestCatalogService.getAll();

        model.addAttribute("labResults", labResults);
        model.addAttribute("labTestCatalogs", labTestCatalogs);

        model.addAttribute("resultId", resultId);
        model.addAttribute("testName", testName);
        model.addAttribute("date", date);

        return "technician/result-list";
    }

}
