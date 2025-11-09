package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.entity.Appointment;
import com.group4.clinicmanagement.entity.Bill;
import com.group4.clinicmanagement.repository.BillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class BillService {
    private final BillRepository billRepository;

    public BillService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    public Bill getBillByAppointmentId(Integer appointmentId) {
        return billRepository.findByAppointment_AppointmentId(appointmentId);
    }

    public boolean existsByAppointmentId(Integer appointmentId) {
        return billRepository.existsByAppointment_AppointmentId(appointmentId);
    }

    @Transactional
    public Bill createBillForAppointment(Appointment appointment) {
        Bill bill = new Bill();
        bill.setAppointment(appointment);
        bill.setCreatedAt(LocalDateTime.now());
        bill.getAmount();
        return billRepository.save(bill);
    }

    public Bill getBillById(int billId) {
        return billRepository.findById(billId).orElse(null);
    }
}
