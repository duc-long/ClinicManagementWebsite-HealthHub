package com.group4.clinicmanagement.controller.technician;

import com.group4.clinicmanagement.dto.LabRequestDTO;
import com.group4.clinicmanagement.dto.LabResultDTO;
import com.group4.clinicmanagement.entity.LabRequest;
import com.group4.clinicmanagement.entity.LabResult;
import com.group4.clinicmanagement.entity.LabTestCatalog;
import com.group4.clinicmanagement.security.CustomUserDetails;
import com.group4.clinicmanagement.service.CustomUserDetailsService;
import com.group4.clinicmanagement.service.LabRequestService;
import com.group4.clinicmanagement.service.LabResultService;
import com.group4.clinicmanagement.service.LabTestCatalogService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
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

    @GetMapping("/create-test/{id}")
    public String createTest(@PathVariable("id") Integer labRequestId, Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        CustomUserDetails userDetails = authentication.getPrincipal() == null ? null : (CustomUserDetails) authentication.getPrincipal();
        int technicianId = userDetails.getUserId();

        Integer resultId = labResultService.createResultForRequest(labRequestId, technicianId);

        return "redirect:/technician/result/" + resultId;
    }

    @GetMapping(value = "/result/{id}")
    public String resultDetail(@PathVariable(name = "id") String id,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        try {
            int resultId = Integer.parseInt(id);
            LabResultDTO result = labResultService.findById(resultId);

            if (result == null) {
                model.addAttribute("mess", "Result not found!!!");
                return "technician/result-detail";
            }
            model.addAttribute("result", result);

            return "technician/result-detail";
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid request id");
            return "redirect:/technician/result-list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Something wrong");
            return "redirect:/technician/result-list";
        }
    }

    @GetMapping("/result-list")
    public String testList(
            @RequestParam(required = false) String resultId,
            @RequestParam(required = false) String testName,
            @RequestParam(required = false) String viewAll,
            Model model) {

        try {
            resultId = (resultId != null && !resultId.isBlank()) ? resultId.trim() : null;
            testName = (testName != null && !testName.isBlank()) ? testName.trim() : null;
            boolean isAll = "true".equalsIgnoreCase(viewAll);

            if (viewAll != null && !"true".equalsIgnoreCase(viewAll) && !"false".equalsIgnoreCase(viewAll)) {
                isAll = true;
            }

            List<LabResultDTO> resultDTOS;

            if (resultId == null && testName == null && isAll) {
                resultDTOS = labResultService.findLabResultList();
            } else {
                resultDTOS = labResultService.filterResults(resultId, testName, isAll);
            }
            for (LabResultDTO labResultDTO : resultDTOS) {
                System.out.println(labResultDTO.getResultText());
            }

            List<LabTestCatalog> labTestCatalogs = labTestCatalogService.getAll();
            LocalDate now = LocalDate.now();

            model.addAttribute("labResults", resultDTOS);
            model.addAttribute("labTestCatalogs", labTestCatalogs);

            model.addAttribute("resultId", resultId);
            model.addAttribute("testName", testName);
            model.addAttribute("viewAll", isAll);
            model.addAttribute("date", now);

            return "technician/result-list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "technician/result-list";
        }
    }

    @GetMapping(value = "/result-edit/{id}")
    public String updateForm(@PathVariable(name = "id") String id, Model model,  RedirectAttributes redirectAttributes) {

        try {
            int resultId = Integer.parseInt(id);
            LabResultDTO result = labResultService.findById(resultId);

            if (result == null) {
                model.addAttribute("mess", "Result not found!!!");
                return "technician/result-edit";
            }

            model.addAttribute("result", result);

            return "technician/result-edit";
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid request id");
            return "redirect:/technician/result-list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Something wrong");
            return "redirect:/technician/result-list";
        }
    }

    @PostMapping("/result-edit/{id}")
    public String updateResult(
            @PathVariable int id,
            @Valid @ModelAttribute("result") LabResultDTO dto,
            BindingResult bindingResult,
            @RequestParam(value = "xrayFiles", required = false) List<MultipartFile> xrayFiles,
            @RequestParam(value = "deleteImageIds", required = false) List<Integer> deleteImageIds,
            @RequestParam(value = "imageIds", required = false) List<Integer> imageIds,
            @RequestParam(value = "imageDescriptions", required = false) List<String> imageDescriptions,
            @RequestParam(value = "newDescriptions", required = false) List<String> newDescriptions,
            RedirectAttributes redirectAttributes, //message
            Model model) throws IOException {
            try {
                if (bindingResult.hasErrors()) {
                    model.addAttribute("result", dto);
                    return "technician/result-edit";
                }

                dto.setResultId(id);
                labResultService.updateResultWithImages(dto, xrayFiles, deleteImageIds, imageIds, imageDescriptions, newDescriptions);
                redirectAttributes.addFlashAttribute("successMessage", "Result updated successfully!");
            }catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                return "redirect:/technician/result/" + id;
            }
        return "redirect:/technician/result/" + id;
    }

    @GetMapping(value = "/result-confirm/{id}")
    public String confirmForm(@PathVariable(name = "id") String id, Model model,RedirectAttributes redirectAttributes) {
        try {
            int resultId = Integer.parseInt(id);
            LabResultDTO result = labResultService.findById(resultId);

            if (result == null) {
                redirectAttributes.addFlashAttribute("errorMessage","Result not found!!!");
                return "redirect:/technician/result-list";
            }

            model.addAttribute("result", result);

            return "technician/result-confirm";
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid request id");
            return "redirect:/technician/result-list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Something wrong");
            return "redirect:/technician/result-list";
        }
    }

    @PostMapping(value = "/result-confirm/{id}")
    public String confirm(@PathVariable int id,
                          @Valid @ModelAttribute("result") LabResultDTO dto,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        try {
            labResultService.confirmResult(id);
            redirectAttributes.addFlashAttribute("successMessage", "Result confirmed successfully!");
            return "redirect:/technician/result-list";
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid request id");
            return "redirect:/technician/result-list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Something wrong");
            return "redirect:/technician/result-list";
        }
    }

}