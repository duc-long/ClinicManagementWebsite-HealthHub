package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.repositories.CashierRepositories;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

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

    public void save(User user) {
        cashierRepositories.save(user);
    }

}
