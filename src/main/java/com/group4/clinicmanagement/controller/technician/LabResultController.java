package com.group4.clinicmanagement.controller.technician;

import com.group4.clinicmanagement.dto.LabRequestDTO;
import com.group4.clinicmanagement.dto.LabResultDTO;
import com.group4.clinicmanagement.entity.LabRequest;
import com.group4.clinicmanagement.entity.LabResult;
import com.group4.clinicmanagement.entity.LabTestCatalog;
import com.group4.clinicmanagement.enums.LabRequestStatus;
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
        try {
            CustomUserDetails userDetails = authentication.getPrincipal() == null ? null : (CustomUserDetails) authentication.getPrincipal();
            int technicianId = userDetails.getUserId();

            Integer resultId = labResultService.createResultForRequest(labRequestId, technicianId);

            return "redirect:/technician/result/" + resultId;
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Invalid request id");
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid request ID format.");
            return "redirect:/technician/create-test";
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

    @GetMapping(value = "/result-edit/{id}")
    public String updateForm(@PathVariable(name = "id") String id, Model model, RedirectAttributes redirectAttributes) {

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
            @ModelAttribute("result") LabResultDTO dto,
            @RequestParam(value = "xrayFiles", required = false) List<MultipartFile> xrayFiles,
            @RequestParam(value = "deleteImageIds", required = false) List<Integer> deleteImageIds,
            @RequestParam(value = "imageIds", required = false) List<Integer> imageIds,
            @RequestParam(value = "imageDescriptions", required = false) List<String> imageDescriptions,
            @RequestParam(value = "newDescriptions", required = false) List<String> newDescriptions,
            RedirectAttributes redirectAttributes, //message
            Model model) throws IOException {

        try {
            dto.setResultId(id);
            labResultService.updateResultWithImages(dto, xrayFiles, deleteImageIds, imageIds, imageDescriptions, newDescriptions);
            redirectAttributes.addFlashAttribute("successMessage", "Result updated successfully!");

            return "redirect:/technician/result/" + id;
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid request id");
            return "redirect:/technician/result-list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Something wrong");
            return "redirect:/technician/result-list";
        }
    }

    @GetMapping(value = "/result-confirm/{id}")
    public String confirmForm(@PathVariable(name = "id") String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            int resultId = Integer.parseInt(id);
            LabResultDTO result = labResultService.findById(resultId);

            if (result == null) {
                model.addAttribute("mess", "Result not found!!!");
                return "technician/result-edit";
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
    public String confirm(@PathVariable String id,
                          @Valid @ModelAttribute("result") LabResultDTO dto,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        try {
            int resultId = Integer.parseInt(id);
            LabResultDTO result = labResultService.findById(resultId);

            if (result.getResultText() == null || result.getResultText().isEmpty()) {

                redirectAttributes.addFlashAttribute("errorMessage", "Description test must not be empty when confirming result");
                return "redirect:/technician/result-edit/" + id;

            } else if (result.getTestName().equalsIgnoreCase("X-ray") &&
                    (result.getImages() == null || result.getImages().isEmpty())) {

                redirectAttributes.addFlashAttribute("errorMessage", "Test images must not be empty when confirming result");
                return "redirect:/technician/result-edit/" + id;
            }

            labResultService.confirmResult(resultId);
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

    @GetMapping(value = "/result-delete/{id}")
    public String deleteResultForm(@PathVariable(name = "id") String id,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        try {
            int resultId = Integer.parseInt(id);
            LabResultDTO result = labResultService.findById(resultId);

            if (result == null) {
                model.addAttribute("mess", "Result not found!!!");
                return "technician/result-delete";
            }
            if (result.getLabRequestStatus().equalsIgnoreCase(LabRequestStatus.COMPLETED.name())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Result has been completed!");
                return "redirect:/technician/result-list";
            }

            model.addAttribute("result", result);

            return "/technician/result-delete";
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid request id");
            return "redirect:/technician/result-list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Something wrong");
            return "redirect:/technician/result-list";
        }
    }

    @PostMapping(value = "/result-delete/{id}")
    public String deleteResult(@PathVariable(name = "id") int id, RedirectAttributes redirectAttributes) {

        try {
            labResultService.deleteResult(id);
            redirectAttributes.addFlashAttribute("successMessage", "Delete successfully!");
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
