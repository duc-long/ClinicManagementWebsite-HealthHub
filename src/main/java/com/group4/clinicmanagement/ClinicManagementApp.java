package com.group4.clinicmanagement;

import com.group4.clinicmanagement.entity.Appointment;
import com.group4.clinicmanagement.entity.Patient;
import com.group4.clinicmanagement.repository.AppointmentRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

@SpringBootApplication
public class ClinicManagementApp {
    public static void main(String[] args) {
//        SpringApplication.run(ClinicManagementApp.class, args);

        ConfigurableApplicationContext context = SpringApplication.run(ClinicManagementApp.class, args);
        AppointmentRepository appointmentRepository = context.getBean(AppointmentRepository.class);
//
        List<Appointment> appointments = appointmentRepository.findAllByPatient_PatientId(11);

        System.out.println("main");
        for (Appointment appointment : appointments) {
            System.out.println(appointment.getAppointmentId() + " " + appointment.getStatus());
        }
    }
}
