package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.entity.Appointment;
import com.group4.clinicmanagement.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;



    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }


    public List<Appointment> findAllByPatientId(int patientId) {
        return appointmentRepository.findAllByPatient_PatientId(patientId);
    }

    public Appointment findAppointmentById(int appointmentId) {
        return appointmentRepository.findById(appointmentId).orElse(null);
    }

    public Appointment saveAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    public void deleteAppointmentById(int appointmentId) {
        appointmentRepository.deleteById(appointmentId);
    }

    public boolean canBookAppointment(LocalDate date) {
        int count = appointmentRepository.countByAppointmentDate(date);
        return count < 5; // accept make appointment 5 times per day
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRe
        pository.findAll();
    }

    public Appointment getById(Integer id) {
        return appointmentRepository.findById(id).orElse(null);
    }


}
