package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.entity.Appointment;
import com.group4.clinicmanagement.entity.Bill;
import com.group4.clinicmanagement.repository.BillRepository;
import com.group4.clinicmanagement.repository.admin.BillForAdminRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BillService {

    private final BillRepository billRepository;
    private final BillForAdminRepository billForAdminRepository;

    public BillService(BillRepository billRepository, BillForAdminRepository billForAdminRepository) {
        this.billRepository = billRepository;
        this.billForAdminRepository = billForAdminRepository;
    }

    public Double getRevenueToday() {
        return billForAdminRepository.getRevenueByDay(LocalDate.now());
    }

    public Double getRevenueThisMonth() {
        LocalDate now = LocalDate.now();
        return billForAdminRepository.getRevenueByMonth(now.getMonthValue(), now.getYear());
    }

    public Double getRevenueThisYear() {
        return billForAdminRepository.getRevenueByYear(LocalDate.now().getYear());
    }

    public Double getRevenueByDay(LocalDate date) {
        return billForAdminRepository.getRevenueByDay(date);
    }

    public Double getRevenueByMonth(int month, int year) {
        return billForAdminRepository.getRevenueByMonth(month, year);
    }

    public Double getRevenueByYear(int year) {
        return billForAdminRepository.getRevenueByYear(year);
    }

    public Double getRevenue(String filter, Integer month, Integer year) {
        switch (filter.toLowerCase()) {
            case "today":
                return this.getRevenueToday();

            case "month":
                if (month == null) month = LocalDate.now().getMonthValue();
                if (year == null) year = LocalDate.now().getYear();
                return billForAdminRepository.getRevenueByMonth(month, year);

            case "year":
                if (year == null) year = LocalDate.now().getYear();
                return billForAdminRepository.getRevenueByYear(year);

            default:
                return 0.0;
        }
    }

    public List<Double> getRevenueData(String filter) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start;
        List<Object[]> results;

        switch (filter.toLowerCase()) {
            case "today" -> {
                start = now.toLocalDate().atStartOfDay();
                results = billForAdminRepository.getRevenueByDay(start, now);
            }
            case "year" -> {
                start = now.withMonth(1).withDayOfMonth(1).toLocalDate().atStartOfDay();
                results = billForAdminRepository.getRevenueByMonth(start, now);
            }
            default -> { // month
                start = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
                results = billForAdminRepository.getRevenueByDay(start, now);
            }
        }
        return results.stream()
                .map(r -> ((Number) r[1]).doubleValue())
                .toList();
    }

    public Bill getBillByAppointmentId(Integer appointmentId) {
        return billRepository.findByAppointment_AppointmentId(appointmentId);
    }


    public boolean existsByAppointmentId(Integer appointmentId) {
        return billRepository.existsByAppointment_AppointmentId(appointmentId);
    }

    public List<Integer> getLabels(String filter) {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime start;
        List<Object[]> results;

        switch (filter.toLowerCase()) {
            case "today" -> {
                start = now.toLocalDate().atStartOfDay();
                results = billForAdminRepository.getRevenueByDay(start, now);
            }
            case "year" -> {
                start = now.withMonth(1).withDayOfMonth(1).toLocalDate().atStartOfDay();
                results = billForAdminRepository.getRevenueByYear(start, now);
            }
            default -> { // month
                start = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
                results = billForAdminRepository.getRevenueByMonth(start, now);
            }
        }
        return results.stream()
                .map(r -> ((Number) r[0]).intValue())
                .toList();
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




