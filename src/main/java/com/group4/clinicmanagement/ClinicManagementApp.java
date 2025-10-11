package com.group4.clinicmanagement;

import com.group4.clinicmanagement.entity.Appointment;
import com.group4.clinicmanagement.repository.AppointmentRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

@SpringBootApplication
public class ClinicManagementApp {
    public static void main(String[] args) {
        SpringApplication.run(ClinicManagementApp.class, args);

//        ConfigurableApplicationContext context = SpringApplication.run(ClinicManagementApp.class, args);
//        AppointmentRepository appointmentRepository = context.getBean(AppointmentRepository.class);
////
//        Appointment appointment = appointmentRepository.findById(11).orElse(null);
//        System.out.println(appointment.getAppointmentId() + " " + appointment.getStatus());
    }
}
