package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.entity.Appointment;
import com.group4.clinicmanagement.repository.AppointmentRepository;
import org.springframework.stereotype.Service;


import java.time.LocalDate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;

    // constructor
    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    // method to find all Appointment by patient ID
    public List<Appointment> findAllByPatientId(int patientId) {
        return appointmentRepository.findAllByPatient_PatientId(patientId);
    }

    // method to find Appointment by appointment ID
    public Appointment findAppointmentById(int appointmentId) {
        return appointmentRepository.findById(appointmentId).orElse(null);
    }

    // method to save Appointment to DB
    public Appointment saveAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    // method to delete Appointment
    public void deleteAppointmentById(int appointmentId) {
        appointmentRepository.deleteById(appointmentId);
    }

    // method to check if can book Appointment
    public boolean canBookAppointment(int patientId) {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        int count = appointmentRepository.countAppointmentsInDay(patientId, today);
        return count < 5; // accept make appointment 5 times per day
    }

    // method to get all Appointment
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    // method to get Appointment by ID
    public Appointment getById(Integer id) {
        return appointmentRepository.findById(id).orElse(null);
    }

    // method to find all Appointment
    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    // method to get Appointment by ID
    public Appointment findById(int id) {
        return appointmentRepository.findById(id).get();
    }

    // method to get all Appointment by Appointment's status
    public List<Appointment> findByStatus(Integer status) {
        return appointmentRepository.findByStatusValueOrderByDoctor_DoctorIdAsc(status);
    }

    // method to check if the booking date is after current date
    public boolean isBookAppointmentVailDate(LocalDate date) {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);

        return !date.isBefore(today);
    }

}
