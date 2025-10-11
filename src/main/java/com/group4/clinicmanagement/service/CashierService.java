package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.repository.CashierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CashierService {
    CashierRepository cashierRepository;

    public CashierService(CashierRepository cashierRepository) {
        this.cashierRepository = cashierRepository;
    }

    @Transactional
    public User findUserById(int id) {
        return cashierRepository.getReferenceById(id);
    }

    @Transactional
    public void save(User user) {
        cashierRepository.save(user);
    }

}
