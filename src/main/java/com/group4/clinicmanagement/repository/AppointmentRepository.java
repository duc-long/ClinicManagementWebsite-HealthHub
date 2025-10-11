package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findByStatusIn(List<Integer> statuses);
}

