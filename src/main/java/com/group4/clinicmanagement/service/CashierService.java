package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.repository.CashierRepositories;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CashierService {
    CashierRepositories cashierRepositories;

    public CashierService(CashierRepositories cashierRepositories) {
        this.cashierRepositories = cashierRepositories;
    }

    @Transactional
    public User findUserById(int id) {
        return cashierRepositories.getReferenceById(id);
    }

    @Transactional
    public void save(User user) {
        cashierRepositories.save(user);
    }

}
