package com.group4.clinicmanagement.repositories;

import com.group4.clinicmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashierRepositories extends JpaRepository<User,Integer> {
}
