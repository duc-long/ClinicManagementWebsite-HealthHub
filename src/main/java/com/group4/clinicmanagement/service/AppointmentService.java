package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.entity.Appointment;
import com.group4.clinicmanagement.entity.Doctor;
import com.group4.clinicmanagement.entity.DoctorDailySlot;
import com.group4.clinicmanagement.enums.AppointmentStatus;
import com.group4.clinicmanagement.repository.AppointmentRepository;
import com.group4.clinicmanagement.repository.DoctorDailySlotRepository;
import com.group4.clinicmanagement.repository.DoctorRepository;
import com.group4.clinicmanagement.repository.FeedbackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorDailySlotRepository slotRepository;
    private final DoctorRepository doctorRepository;
    private final FeedbackRepository feedbackRepository;
    // constructor
    public AppointmentService(AppointmentRepository appointmentRepository, DoctorDailySlotRepository slotRepository, DoctorRepository doctorRepository,FeedbackRepository feedbackRepository) {

            this.feedbackRepository = feedbackRepository;
            this.appointmentRepository = appointmentRepository;
            this.slotRepository = slotRepository;
            this.doctorRepository = doctorRepository;
        }

        // method to find all Appointment by patient ID
        public List<Appointment> findAllByPatientId ( int patientId){
            return appointmentRepository.findAllByPatient_PatientId(patientId);
        }

        // method to find Appointment by appointment ID
        public Appointment findAppointmentById ( int appointmentId){
            return appointmentRepository.findById(appointmentId).orElse(null);
        }

        // method to save Appointment to DB
        public Appointment saveAppointment (Appointment appointment){
            return appointmentRepository.save(appointment);
        }

        // method to delete Appointment
        public void deleteAppointmentById ( int appointmentId){
            appointmentRepository.deleteById(appointmentId);
        }

        // method to check if can book Appointment
        public boolean canBookAppointment ( int patientId){
            LocalDate today = LocalDate.now(ZoneOffset.UTC);
            int count = appointmentRepository.countAppointmentsInDay(patientId, today);
            return count < 5; // accept make appointment 5 times per day
        }

        // method to get all Appointment
        public List<Appointment> getAllAppointments () {
            return appointmentRepository.findAll();
        }

        // method to get Appointment by ID
        public Appointment getById (Integer id){
            return appointmentRepository.findById(id).orElse(null);
        }

        // method to find all Appointment
        public List<Appointment> findAll () {
            return appointmentRepository.findAll();
        }

        // method to get Appointment by ID
        public Appointment findById ( int id){
            return appointmentRepository.findById(id).get();
        }

        // method to get all Appointment by Appointment's status
        public List<Appointment> findByStatus (Integer status){
            return appointmentRepository.findByStatusValueOrderByDoctor_DoctorIdAsc(status);
        }

        // method to check if the booking date is after current date
        public boolean isBookAppointmentVailDate (LocalDate date){
            LocalDate today = LocalDate.now(ZoneOffset.UTC);

            return !date.isBefore(today);
        }

        public List<Doctor> getAvailableDoctors (LocalDate date){
            return doctorRepository.findAvailableDoctorsOnDate(date);
        }

        @Transactional
        public void updateAppointment (Appointment updated){
            Appointment existing = appointmentRepository.findById(updated.getAppointmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

            AppointmentStatus oldStatus = existing.getStatus();
            AppointmentStatus newStatus = updated.getStatus();

            Doctor oldDoctor = existing.getDoctor();
            Doctor newDoctor = updated.getDoctor();
            LocalDate date = existing.getAppointmentDate();

            existing.setCancelReason(updated.getCancelReason());
            existing.setNotes(updated.getNotes());
            existing.setDoctor(newDoctor);
            existing.setStatus(newStatus);

            // Case 1: Assign doctor & was pending → auto CONFIRM + slot -1
            if (oldStatus == AppointmentStatus.PENDING && newDoctor != null) {
                existing.setStatus(AppointmentStatus.CONFIRMED);

                DoctorDailySlot slot = slotRepository.findByDoctorAndSlotDate(newDoctor, date)
                        .orElseGet(() -> {
                            DoctorDailySlot newSlot = new DoctorDailySlot();
                            newSlot.setDoctor(newDoctor);
                            newSlot.setSlotDate(date);
                            newSlot.setTotalSlots(20);
                            newSlot.setAvailableSlots(20);
                            return slotRepository.save(newSlot);
                        });

                if (slot.getAvailableSlots() <= 0) {
                    throw new IllegalStateException("Doctor has no available slots on " + date);
                }

                slot.setAvailableSlots(slot.getAvailableSlots() - 1);
                slotRepository.save(slot);
            }

            //Case 2: Cancel confirmed appointment → slot +1
            if (newStatus == AppointmentStatus.CANCELLED && oldStatus == AppointmentStatus.CONFIRMED && oldDoctor != null) {
                DoctorDailySlot slot = slotRepository.findByDoctorAndSlotDate(oldDoctor, date).orElse(null);
                if (slot != null && slot.getAvailableSlots() < slot.getTotalSlots()) {
                    slot.setAvailableSlots(slot.getAvailableSlots() + 1);
                    slotRepository.save(slot);
                }
            }

            appointmentRepository.save(existing);
        }

    }
