package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.VitalSignsDTO;
import com.group4.clinicmanagement.entity.Appointment;
import com.group4.clinicmanagement.entity.VitalSigns;
import com.group4.clinicmanagement.repository.AppointmentRepository;
import com.group4.clinicmanagement.repository.VitalSignsRepository;

public class VitalSignsService {
    private final VitalSignsRepository vitalSignsRepository;
    private final AppointmentRepository  appointmentRepository;

    public VitalSignsService(VitalSignsRepository vitalSignsRepository, AppointmentRepository appointmentRepository) {
        this.vitalSignsRepository = vitalSignsRepository;
        this.appointmentRepository = appointmentRepository;
    }

//    public void saveVitalSigns(VitalSignsDTO dto) {
//        Appointment appointment = appointmentRepository.findById(dto.getAppointmentId())
//                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
//
//        VitalSigns vs = new VitalSigns();
//        vs.setAppointment(appointment);
//        vs.setTemperature(dto.getTemperature());
//        vs.setHeartRate(dto.getHeartRate());
//        vs.setSystolic(dto.getSystolic());
//        vs.setDiastolic(dto.getDiastolic());
//        vs.setWeight(dto.getWeight());
//        vs.setHeight(dto.getHeight());
//
//        // ✅ Auto calculate BMI
//        if (dto.getHeight() != null && dto.getWeight() != null && dto.getHeight() > 0)
//            vs.setBmi(dto.getWeight() / Math.pow(dto.getHeight() / 100, 2));
//
//        vs.setCreatedAt(LocalDateTime.now());
//        vitalSignsRepository.save(vs);
//    }
}
