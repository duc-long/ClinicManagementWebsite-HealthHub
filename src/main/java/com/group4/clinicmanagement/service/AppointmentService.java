package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.AppointmentDTO;
import com.group4.clinicmanagement.dto.ReceptionistAppointmentDTO;
import com.group4.clinicmanagement.entity.Appointment;
import com.group4.clinicmanagement.entity.Doctor;
import com.group4.clinicmanagement.entity.DoctorDailySlot;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.enums.AppointmentStatus;
import com.group4.clinicmanagement.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorDailySlotRepository slotRepository;
    private final DoctorRepository doctorRepository;
    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    // constructor
    public AppointmentService(AppointmentRepository appointmentRepository, DoctorDailySlotRepository slotRepository, DoctorRepository doctorRepository, FeedbackRepository feedbackRepository, UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.slotRepository = slotRepository;
        this.doctorRepository = doctorRepository;
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
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

    public List<Doctor> getAvailableDoctors(Integer departmentId, LocalDate date) {
        if (departmentId == null) {
            return List.of();
        }
        if (date == null) {
            return List.of();
        }
        return doctorRepository.findAvailableDoctors(departmentId, date);
    }

    @Transactional
    public void scheduleAppointment(Appointment updated, User receptionist) {
        Appointment existing = appointmentRepository.findById(updated.getAppointmentId())
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        if (existing.getStatus() != AppointmentStatus.PENDING) {
            throw new IllegalStateException("Only pending appointments can be scheduled");
        }
        AppointmentStatus oldStatus = existing.getStatus();
        AppointmentStatus newStatus = updated.getStatus();

        Doctor newDoctor = updated.getDoctor();
        LocalDate date = existing.getAppointmentDate();


        existing.setReceptionist(receptionist);
        existing.setCancelReason(updated.getCancelReason());
        existing.setDoctor(newDoctor);
        existing.setStatus(newStatus);
        existing.fillStatusValue();

        // Assign doctor & was pending → auto CONFIRM + slot -1
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

        appointmentRepository.save(existing);
    }

    @Transactional
    public void checkInAppointment(int appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed appointments can be checked in.");
        }

        Doctor doctor = appointment.getDoctor();
        LocalDate date = appointment.getAppointmentDate();

        if (doctor == null) {
            throw new IllegalStateException("Cannot check in — appointment has no assigned doctor.");
        }

        Integer maxQueue = appointmentRepository.findMaxQueueNumber(doctor.getDoctorId(), date);
        int nextQueue = (maxQueue == null ? 1 : maxQueue + 1);

        appointment.setQueueNumber(nextQueue);
        appointment.setStatus(AppointmentStatus.CHECKED_IN);
        appointment.fillStatusValue();

        appointmentRepository.save(appointment);
    }


    public int countTodayAppointments(Integer doctorId) {
        return appointmentRepository.countTodayAppointments(doctorId);
    }

    public int countWaitingAppointments(Integer doctorId) {
        int totalToday = appointmentRepository.countTodayAppointments(doctorId);
        int examinedToday = appointmentRepository.countExaminedTodayAppointments(doctorId);
        return totalToday - examinedToday;
    }

    public Page<AppointmentDTO> getTodayAppointmentsPaged(Integer doctorId, String patientName, AppointmentStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Appointment> appointments = appointmentRepository.findTodayAppointmentsPaged(doctorId, patientName, status, pageable);

        return appointments
                .map(a -> new AppointmentDTO(
                        a.getAppointmentId(),
                        a.getPatient().getPatientId(),
                        a.getDoctor().getUser().getFullName(),
                        a.getPatient().getUser().getFullName(),
                        a.getReceptionist() != null ? a.getReceptionist().getFullName() : "N/A",
                        a.getAppointmentDate(),
                        a.getCreatedAt(),
                        a.getStatus(),
                        a.getQueueNumber(),
                        a.getNotes(),
                        a.getCancelReason()
                ));
    }

    public Page<ReceptionistAppointmentDTO> getStatusAppointmentPage(int statusValue, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);
        Page<Appointment> appointments = appointmentRepository.findStatusValueDesc(statusValue, pageable);
        return appointments
                .map(a -> new ReceptionistAppointmentDTO(
                        a.getAppointmentId(),
                        a.getPatient().getPatientId(),
                        a.getDoctor() != null && a.getDoctor().getUser() != null ? a.getDoctor().getUser().getFullName() : "Not Assigned",
                        a.getPatient() != null && a.getPatient().getUser() != null ? a.getPatient().getUser().getFullName() : "Unknown",
                        a.getReceptionist() != null ? a.getReceptionist().getFullName() : "N/A",
                        a.getPatient().getUser().getPhone(),
                        a.getAppointmentDate(),
                        a.getCreatedAt(),
                        AppointmentStatus.fromInt(a.getStatusValue()),
                        a.getQueueNumber() != null ? a.getQueueNumber() : 0,
                        a.getNotes(),
                        a.getCancelReason(),
                        (a.getStatusValue() == AppointmentStatus.CONFIRMED.getValue() //check checkin
                                && a.getAppointmentDate().equals(LocalDate.now()))
                ));
    }

    public Appointment getAppointmentForReceptionist(int id) {
        return appointmentRepository.findIdWithStatusRange(id).orElse(null);
    }
}

