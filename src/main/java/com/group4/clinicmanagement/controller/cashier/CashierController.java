package com.group4.clinicmanagement.controller.cashier;

import com.group4.clinicmanagement.dto.CashierUserDTO;
import com.group4.clinicmanagement.dto.RecepCashAppointmentDTO;
import com.group4.clinicmanagement.dto.UserDTO;
import com.group4.clinicmanagement.entity.Appointment;
import com.group4.clinicmanagement.entity.Bill;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.enums.AppointmentStatus;
import com.group4.clinicmanagement.repository.UserRepository;
import com.group4.clinicmanagement.service.*;
import com.group4.clinicmanagement.service.CashierService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String editProfile(@ModelAttribute("cashier") CashierUserDTO dto, Authentication authentication) {
        String cashierName = authentication.getName();
        cashierService.updateCashierProfile(cashierName, dto);
        return "redirect:/cashier/profile";
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
                    appointments = appointmentService.getStatusAppointmentPage(AppointmentStatus.PAID.getValue(), currentPage, size);
            default ->
                    appointments = appointmentService.getStatusAppointmentPage(AppointmentStatus.CHECKED_IN.getValue(), currentPage, size);
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
            Appointment appointment = appointmentService.getAppointmentForReceptionist(appointmentId);
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


    @PostMapping("/appointment/{id}/create-bill")
    public String createBill(@PathVariable("id") int appointmentId, RedirectAttributes redirectAttributes) {
        try {
            Appointment appointment = appointmentService.getById(appointmentId);
            if (appointment == null) {
                throw new IllegalArgumentException("Appointment not found");
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

            Bill newBill = billService.createBillForAppointment(appointment);
            redirectAttributes.addFlashAttribute("message", "Bill created successfully!");
            redirectAttributes.addFlashAttribute("messageType", "success");
            return "redirect:/cashier/bill/" + newBill.getBillId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error while creating bill.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/cashier/appointment-list?status=CHECKED_IN";
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

}