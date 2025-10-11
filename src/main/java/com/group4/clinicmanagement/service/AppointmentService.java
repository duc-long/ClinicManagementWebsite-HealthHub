package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.entity.Appointment;
import com.group4.clinicmanagement.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {
    AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }
    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }
    public Appointment findById(int id) {
        return appointmentRepository.findById(id).get();
    }
}
