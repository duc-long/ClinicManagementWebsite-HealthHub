package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.repository.admin.BillRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BillService {

    private final BillRepository billRepository;

    public BillService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    public Double getRevenueToday() {
        return billRepository.getRevenueByDay(LocalDate.now());
    }

    public Double getRevenueThisMonth() {
        LocalDate now = LocalDate.now();
        return billRepository.getRevenueByMonth(now.getMonthValue(), now.getYear());
    }

    public Double getRevenueThisYear() {
        return billRepository.getRevenueByYear(LocalDate.now().getYear());
    }

    public Double getRevenueByDay(LocalDate date) {
        return billRepository.getRevenueByDay(date);
    }

    public Double getRevenueByMonth(int month, int year) {
        return billRepository.getRevenueByMonth(month, year);
    }

    public Double getRevenueByYear(int year) {
        return billRepository.getRevenueByYear(year);
    }

    public Double getRevenue(String filter, Integer month, Integer year) {
        switch (filter.toLowerCase()) {
            case "today":
                return this.getRevenueToday();

            case "month":
                if (month == null) month = LocalDate.now().getMonthValue();
                if (year == null) year = LocalDate.now().getYear();
                return billRepository.getRevenueByMonth(month, year);

            case "year":
                if (year == null) year = LocalDate.now().getYear();
                return billRepository.getRevenueByYear(year);

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
                results = billRepository.getRevenueByDay(start, now);
            }
            case "year" -> {
                start = now.withMonth(1).withDayOfMonth(1).toLocalDate().atStartOfDay();
                results = billRepository.getRevenueByMonth(start, now);
            }
            default -> { // month
                start = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
                results = billRepository.getRevenueByDay(start, now);
            }
        }

        return results.stream()
                .map(r -> ((Number) r[1]).doubleValue())
                .toList();
    }

    public List<Integer> getLabels(String filter) {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime start;
        List<Object[]> results;

        switch (filter.toLowerCase()) {
            case "today" -> {
                start = now.toLocalDate().atStartOfDay();
                results = billRepository.getRevenueByDay(start, now);
            }
            case "year" -> {
                start = now.withMonth(1).withDayOfMonth(1).toLocalDate().atStartOfDay();
                results = billRepository.getRevenueByYear(start, now);
            }
            default -> { // month
                start = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
                results = billRepository.getRevenueByMonth(start, now);
            }
        }

        return results.stream()
                .map(r -> ((Number) r[0]).intValue())
                .toList();
    }

}




