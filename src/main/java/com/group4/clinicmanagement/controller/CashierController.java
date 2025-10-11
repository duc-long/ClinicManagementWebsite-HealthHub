package com.group4.clinicmanagement.controller;

import com.group4.clinicmanagement.dto.UserDTO;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.service.CashierService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/cashier")
public class CashierController {
    CashierService cashierService;

    private final int id = 18;

    public CashierController(CashierService cashierService) {
        this.cashierService = cashierService;
    }

    @GetMapping(value = "/view-profile")
    public String viewProfile( Model model) {
        User user = cashierService.findUserById(id);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getUserId());
        userDTO.setFullName(user.getFullName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhone(user.getPhone());
        userDTO.setGender(user.getGender());
        model.addAttribute("userDTO", userDTO);
        return "cashier/view-profile";
    }
    @GetMapping("/edit-profile")
    public String showEditForm( Model model) {

        User user = cashierService.findUserById(id);

        UserDTO dto = new UserDTO();
        dto.setId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setGender(user.getGender());

        model.addAttribute("userDTO", dto);
        return "cashier/edit-profile";
    }
    @PostMapping(value = "/edit-profile")
    public String editProfile(@ModelAttribute("userDTO") UserDTO dto) {
        User user = cashierService.findUserById(id);

        System.out.println("Before update: " + user.getGender());
        System.out.println("Form value: " + dto.getGender());

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setGender(dto.getGender());

        cashierService.save(user);

        System.out.println("After update (in memory): " + user.getGender());
        return "redirect:/cashier/view-profile";
    }

}
