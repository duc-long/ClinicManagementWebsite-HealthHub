package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.CashierUserDTO;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.repository.CashierRepository;
import com.group4.clinicmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CashierService {
    private final  CashierRepository cashierRepository;

    public CashierService(CashierRepository cashierRepository) {
        this.cashierRepository = cashierRepository;
    }

    private CashierUserDTO convertToDTO(User user) {
        if (user == null) return null;
        return new CashierUserDTO(
                user.getUserId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getGender(),
                user.getAvatar(),
                user.getStatus()
        );
    }

    private void applyDTOToEntity(CashierUserDTO dto, User user) {
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setGender(dto.getGender());
    }

    @Transactional(readOnly = true)
    public CashierUserDTO getCashierProfile(String cashierName) {
        User user = cashierRepository.findByUsername(cashierName);
        System.out.println("\n====="+ user.getUsername());
        return convertToDTO(user);
    }

    @Transactional
    public void updateCashierProfile(String cashierName, CashierUserDTO dto) {
        User user = cashierRepository.findByUsername(cashierName);
        System.out.println("\n====="+ dto.getUsername());
        applyDTOToEntity(dto, user);
        cashierRepository.save(user);
    }
}
