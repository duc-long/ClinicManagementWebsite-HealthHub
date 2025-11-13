package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.CashierUserDTO;
import com.group4.clinicmanagement.entity.Staff;
import com.group4.clinicmanagement.repository.CashierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CashierService {
    private final  CashierRepository cashierRepository;

    public CashierService(CashierRepository cashierRepository) {
        this.cashierRepository = cashierRepository;
    }

    private CashierUserDTO convertToDTO(Staff user) {
        if (user == null) return null;
        return new CashierUserDTO(
                user.getStaffId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getGender(),
                user.getAvatar(),
                user.getStatus()
        );
    }

    private void applyDTOToEntity(CashierUserDTO dto, Staff user) {
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setGender(dto.getGender());
    }

    @Transactional(readOnly = true)
    public CashierUserDTO getCashierProfile(String cashierName) {
        Staff user = cashierRepository.findByUsername(cashierName);
        return convertToDTO(user);
    }

    @Transactional
    public void updateCashierProfile(String cashierName, CashierUserDTO dto) {
        Staff user = cashierRepository.findByUsername(cashierName);
        applyDTOToEntity(dto, user);
        cashierRepository.save(user);
    }
}
