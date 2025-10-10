package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {

    List<Bill> findByPatient_UserId(Long userId);

    List<Bill> findByStatus(Integer status);
}
