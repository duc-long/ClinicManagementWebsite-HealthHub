package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashierRepository extends JpaRepository<User,Integer> {
}
