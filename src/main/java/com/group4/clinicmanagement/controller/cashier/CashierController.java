package com.group4.clinicmanagement.controller.cashier;

import com.group4.clinicmanagement.dto.CashierLabRequestDTO;
import com.group4.clinicmanagement.dto.CashierUserDTO;
import com.group4.clinicmanagement.dto.RecepCashAppointmentDTO;
import com.group4.clinicmanagement.dto.UserDTO;
import com.group4.clinicmanagement.entity.Appointment;
import com.group4.clinicmanagement.entity.Bill;
import com.group4.clinicmanagement.entity.LabRequest;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.enums.AppointmentStatus;
import com.group4.clinicmanagement.enums.LabRequestStatus;
import com.group4.clinicmanagement.repository.UserRepository;
import com.group4.clinicmanagement.service.*;
import com.group4.clinicmanagement.service.CashierService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/cashier")
public class CashierController {
    private final CashierService cashierService;
    private final AppointmentService appointmentService;
    private final DepartmentService departmentService;
    private final UserRepository userRepository;
    private final BillService billService;
    private final LabRequestService labRequestService;

    public CashierController(CashierService cashierService, AppointmentService appointmentService, DepartmentService departmentService, UserRepository userRepository, BillService billService, LabRequestService labRequestService) {
        this.cashierService = cashierService;
        this.appointmentService = appointmentService;
        this.departmentService = departmentService;
        this.userRepository = userRepository;
        this.billService = billService;
        this.labRequestService = labRequestService;
    }


    @GetMapping("/profile")
    public String viewProfile(Model model, Authentication authentication) {
        String cashierName = authentication.getName();
        CashierUserDTO dto = cashierService.getCashierProfile(cashierName);
        model.addAttribute("cashier", dto);
        return "cashier/profile";
    }

    @GetMapping("/edit-profile")
    public String editProfile(Model model, Authentication authentication) {
        String cashierName = authentication.getName();
        CashierUserDTO dto = cashierService.getCashierProfile(cashierName);
        model.addAttribute("cashier", dto);
        return "cashier/edit-profile";
    }

    @PostMapping("/edit-profile")
    public String editProfile(
            @Valid @ModelAttribute("cashier") CashierUserDTO dto,
            BindingResult bindingResult,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("cashier", dto);
            return "cashier/edit-profile";
        }

        try {
            String cashierName = authentication.getName();
            cashierService.updateCashierProfile(cashierName, dto, avatarFile);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            return "redirect:/cashier/profile";

        } catch (IllegalArgumentException e) {
            // Lỗi file ảnh (loại file / kích thước)
            model.addAttribute("cashier", dto);
            model.addAttribute("fileError", e.getMessage());
            return "cashier/edit-profile";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unexpected error while saving profile.");
            return "redirect:/cashier/profile";
        }
    }

    @GetMapping("/appointment-list")
    public String listAppointments(
            @RequestParam(required = false, defaultValue = "CHECKED_IN") String status,
            @RequestParam(defaultValue = "1") String page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        int currentPage = 1;
        try {
            currentPage = Integer.parseInt(page);
            if (currentPage < 1) currentPage = 1;
        } catch (NumberFormatException e) {
            currentPage = 1;
        }

        Page<RecepCashAppointmentDTO> appointments;
        switch (status.toUpperCase()) {
            case "PAID" ->
                    appointments = appointmentService.getStatusAppointmentPageforCash(AppointmentStatus.PAID.getValue(), currentPage, size);
            default ->
                    appointments = appointmentService.getStatusAppointmentPageforCash(AppointmentStatus.CHECKED_IN.getValue(), currentPage, size);
        }

        // gắn billId và canCreateBill
        appointments.forEach(a -> {
            var bill = billService.getBillByAppointmentId(a.getAppointmentId());
            if (bill == null) {
                a.setCanCheck(true); // có thể tạo bill
                a.setBillId(null);
            } else {
                a.setCanCheck(false);
                a.setBillId(bill.getBillId());
            }
        });

        model.addAttribute("appointments", appointments);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("activeStatus", status.toUpperCase());
        return "cashier/appointment-list";
    }


    @GetMapping("/appointment/{id}")
    public String viewAppointmentDetails(@PathVariable("id") String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            int appointmentId = Integer.parseInt(id);
            Appointment appointment = appointmentService.getAppointmentForCashier(appointmentId);
            if (appointment == null) {
                redirectAttributes.addFlashAttribute("message", "Appointment not found");
                redirectAttributes.addFlashAttribute("messageType", "error");
                return "redirect:/cashier/appointment-list";

            }
            model.addAttribute("appointment", appointment);
            return "cashier/appointment-details";

        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("message", "Invalid appointment ID format.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/cashier/appointment-list";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "An unexpected error occurred during check-in.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/cashier/appointment-list";

        }
    }

    @GetMapping("/lab-request-list")
    public String listLabRequests(
            @RequestParam(required = false, defaultValue = "REQUESTED") String status,
            @RequestParam(defaultValue = "1") String page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        int currentPage;
        try {
            currentPage = Math.max(Integer.parseInt(page), 1);
        } catch (NumberFormatException e) {
            currentPage = 1;
        }

        Page<CashierLabRequestDTO> labRequests;
        switch (status.toUpperCase()) {
            case "PAID" -> labRequests =
                    labRequestService.getStatusLabRequestPage(LabRequestStatus.PAID.getValue(), currentPage, size);
            default -> labRequests =
                    labRequestService.getStatusLabRequestPage(LabRequestStatus.REQUESTED.getValue(), currentPage, size);
        }

        model.addAttribute("labRequests", labRequests);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("activeStatus", status.toUpperCase());
        return "cashier/lab-request-list";
    }

    @GetMapping("/lab-request/{id}")
    public String viewLabRequestDetails(
            @PathVariable("id") String id,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            int labRequestId = Integer.parseInt(id);
            LabRequest labRequest = labRequestService.getById(labRequestId);

            if (labRequest == null) {
                redirectAttributes.addFlashAttribute("message", "Lab Request not found.");
                redirectAttributes.addFlashAttribute("messageType", "error");
                return "redirect:/cashier/lab-request-list";
            }

            Bill bill = billService.getBillByLabRequestId(labRequestId);
            model.addAttribute("labRequest", labRequest);
            model.addAttribute("billId", bill != null ? bill.getBillId() : null);

            return "cashier/lab-request-details";

        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("message", "Invalid Lab Request ID format.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/cashier/lab-request-list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error loading Lab Request details.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/cashier/lab-request-list";
        }
    }

    @GetMapping("/bill/{id}")
    public String viewBill(@PathVariable("id") Integer billId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Bill bill = billService.getBillById(billId);
            if (bill == null) {
                redirectAttributes.addFlashAttribute("message", "Bill not found.");
                redirectAttributes.addFlashAttribute("messageType", "error");
                return "redirect:/cashier/appointment-list";
            }

            // Xác định loại bill
            boolean isAppointmentBill = bill.getAppointment() != null;
            boolean isLabBill = bill.getLabRequest() != null;

            model.addAttribute("bill", bill);
            model.addAttribute("isAppointmentBill", isAppointmentBill);
            model.addAttribute("isLabBill", isLabBill);

            return "cashier/bill-details";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error loading bill details.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/cashier/appointment-list";
        }
    }

    @PostMapping("/appointment/{id}/create-bill")
    public String createBillAppointment(@PathVariable("id") int appointmentId,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        try {
            Appointment appointment = appointmentService.getById(appointmentId);
            if (appointment == null) {
                redirectAttributes.addFlashAttribute("message", "Appointment not found.");
                redirectAttributes.addFlashAttribute("messageType", "error");
                return "redirect:/cashier/appointment-list?status=CHECKED_IN";
            }

            if (billService.existsByAppointmentId(appointmentId)) {
                redirectAttributes.addFlashAttribute("message", "Bill already exists for this appointment.");
                redirectAttributes.addFlashAttribute("messageType", "warning");
                return "redirect:/cashier/appointment-list?status=CHECKED_IN";
            }

            if (!appointment.getAppointmentDate().equals(LocalDate.now())) {
                redirectAttributes.addFlashAttribute("message", "You can only create bills for today's appointments.");
                redirectAttributes.addFlashAttribute("messageType", "error");
                return "redirect:/cashier/appointment-list?status=CHECKED_IN";
            }
            User cashier = userRepository.findByUsernameAndRoleId(principal.getName(), 4); // 4 = CASHIER
            if (cashier == null) {
                redirectAttributes.addFlashAttribute("message", "Only cashier accounts can perform this action.");
                redirectAttributes.addFlashAttribute("messageType", "error");
                return "redirect:/cashier/appointment-list?status=CHECKED_IN";
            }


            Bill newBill = billService.createBill(appointmentId, null, cashier);
            redirectAttributes.addFlashAttribute("message", "Bill created successfully!");
            redirectAttributes.addFlashAttribute("messageType", "success");
            return "redirect:/cashier/bill/" + newBill.getBillId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error while creating bill: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/cashier/appointment-list?status=CHECKED_IN";
        }
    }

    @PostMapping("/lab-request/{id}/create-bill")
    public String createBillLabRequest(@PathVariable("id") int labRequestId,
                                       Principal principal,
                                       RedirectAttributes redirectAttributes) {
        try {
            LabRequest labRequest = labRequestService.getById(labRequestId);
            if (labRequest == null) {
                redirectAttributes.addFlashAttribute("message", "Lab Request not found.");
                redirectAttributes.addFlashAttribute("messageType", "error");
                return "redirect:/cashier/lab-request-list?status=REQUESTED";
            }

            if (billService.existsByLabRequestId(labRequestId)) {
                redirectAttributes.addFlashAttribute("message", "Bill already exists for this lab request.");
                redirectAttributes.addFlashAttribute("messageType", "warning");
                return "redirect:/cashier/lab-request-list?status=REQUESTED";
            }

            // Kiểm tra trạng thái phải là REQUESTED
            if (labRequest.getStatus() != LabRequestStatus.REQUESTED) {
                redirectAttributes.addFlashAttribute("message", "Only REQUESTED lab requests can be billed.");
                redirectAttributes.addFlashAttribute("messageType", "error");
                return "redirect:/cashier/lab-request-list?status=REQUESTED";
            }

            // Chỉ cashier mới được tạo bill
            User cashier = userRepository.findByUsernameAndRoleId(principal.getName(), 4);
            if (cashier == null) {
                redirectAttributes.addFlashAttribute("message", "Only cashier accounts can perform this action.");
                redirectAttributes.addFlashAttribute("messageType", "error");
                return "redirect:/cashier/lab-request-list?status=REQUESTED";
            }

            // Tạo bill mới
            Bill newBill = billService.createBill(null, labRequestId, cashier);

            redirectAttributes.addFlashAttribute("message", "Lab Request bill created successfully!");
            redirectAttributes.addFlashAttribute("messageType", "success");
            return "redirect:/cashier/bill/" + newBill.getBillId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error while creating bill: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/cashier/lab-request-list?status=REQUESTED";
        }
    }



    @PostMapping("/bill/{id}/export")
    public String exportBill(@PathVariable("id") Integer billId,
                           RedirectAttributes redirectAttributes,
                           HttpServletResponse response) {
        try {
            Bill bill = billService.exportBill(billId);
            response.setContentType("application/pdf");
            String headerValue = "attachment; filename=bill_" + bill.getBillId() + ".pdf";
            response.setHeader("Content-Disposition", headerValue);

            billService.exportPdfToResponse(bill, response.getOutputStream());
            return "redirect:/cashier/payment-list";
        } catch (Exception e) {
            try {
                response.setContentType("text/plain");
                response.getWriter().write("Error exporting bill: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (Exception ignored) {}
        }
        return "redirect:/cashier/payment-list";
    }


    @GetMapping("/payment-list")
    public String listPayments(
            @RequestParam(required = false, defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "1") String page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        int currentPage;
        try {
            currentPage = Math.max(Integer.parseInt(page), 1);
        } catch (NumberFormatException e) {
            currentPage = 1;
        }

        Page<Bill> bills;
        switch (status.toUpperCase()) {
            case "PENDING" -> bills = billService.getBillsByStatus(0, currentPage, size);
            case "PAID" -> bills = billService.getBillsByStatus(1, currentPage, size);
            default -> bills = billService.getAllBills(currentPage, size);
        }

        model.addAttribute("bills", bills);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("activeStatus", status.toUpperCase());
        return "cashier/payment-list";
    }
}