package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.entity.Appointment;
import com.group4.clinicmanagement.entity.Bill;
import com.group4.clinicmanagement.entity.LabRequest;
import com.group4.clinicmanagement.entity.Staff;
import com.group4.clinicmanagement.enums.AppointmentStatus;
import com.group4.clinicmanagement.enums.BillStatus;
import com.group4.clinicmanagement.enums.LabRequestStatus;
import com.group4.clinicmanagement.repository.AppointmentRepository;
import com.group4.clinicmanagement.repository.BillRepository;
import com.group4.clinicmanagement.repository.LabRequestRepository;
import com.group4.clinicmanagement.util.BillPdfExporter;
import com.group4.clinicmanagement.util.ExamCostUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BillService {

    private final BillRepository billRepository;
    private final LabRequestRepository labRequestRepository;
    private final AppointmentRepository appointmentRepository;
    private final ExamCostUtil examCostUtil;
    private final BillPdfExporter billPdfExporter;

    public BillService(BillRepository billRepository, LabRequestRepository labRequestRepository, AppointmentRepository appointmentRepository, ExamCostUtil examCostUtil, BillPdfExporter billPdfExporter) {
        this.billRepository = billRepository;
        this.labRequestRepository = labRequestRepository;
        this.appointmentRepository = appointmentRepository;
        this.examCostUtil = examCostUtil;
        this.billPdfExporter = billPdfExporter;
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

    public Bill getBillByLabRequestId(Integer labRequestId) {
        return billRepository.findByLabRequest_LabRequestId(labRequestId).orElse(null);
    }

    public Bill getBillById(Integer id) {
        return billRepository.findById(id).orElse(null);
    }

    public Bill getBillByAppointmentId(Integer appointmentId) {
        return billRepository.findByAppointment_AppointmentId(appointmentId);
    }

    public boolean existsByAppointmentId(Integer appointmentId) {
        return billRepository.existsByAppointment_AppointmentId(appointmentId);
    }

    public boolean existsByLabRequestId(Integer labRequestId) {
        return billRepository.existsByLabRequest_LabRequestId(labRequestId);
    }
    @Transactional
    public Bill createBill(Integer appointmentId, Integer labRequestId, Staff cashier) {
        if (appointmentId != null) {
            Appointment appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

            if (appointment.getStatus() != AppointmentStatus.CHECKED_IN) {
                throw new IllegalStateException("Only CHECKED_IN appointments can be billed.");
            }
            if (existsByAppointmentId(appointmentId)) {
                throw new IllegalStateException("Bill already exists for this appointment.");
            }

            Bill bill = new Bill();
            bill.setAppointment(appointment);
            bill.setPatient(appointment.getPatient());
            bill.setAmount(examCostUtil.EXAMCOST); // fixed cost
            bill.setStatus(BillStatus.PENDING);
            bill.setCreatedAt(LocalDateTime.now());
            bill.setCashier(cashier);
            return billRepository.save(bill);
        }

        if (labRequestId != null) {
            LabRequest labRequest = labRequestRepository.findById(labRequestId)
                    .orElseThrow(() -> new IllegalArgumentException("Lab Request not found"));

            if (labRequest.getStatus() != LabRequestStatus.REQUESTED) {
                throw new IllegalStateException("Only REQUESTED lab requests can be billed.");
            }
            if (existsByLabRequestId(labRequestId)) {
                throw new IllegalStateException("Bill already exists for this lab request.");
            }

            Double labCost = 0.0;
            if (labRequest.getTest() != null && labRequest.getTest().getCost() != null) {
                labCost = labRequest.getTest().getCost();
            }

            Bill bill = new Bill();
            bill.setLabRequest(labRequest);
            bill.setPatient(labRequest.getMedicalRecord().getPatient());
            bill.setAmount(labCost);
            bill.setStatus(BillStatus.PENDING);
            bill.setCreatedAt(LocalDateTime.now());
            bill.setCashier(cashier);
            return billRepository.save(bill);
        }

        throw new IllegalArgumentException("No Appointment or LabRequest specified for billing.");
    }

    // Xuất bill (PDF + update trạng thái)
    @Transactional
    public Bill exportBill(Integer billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found"));

        // Cập nhật trạng thái tương ứng
        if (bill.getAppointment() != null) {
            Appointment appointment = bill.getAppointment();
            appointment.setStatus(AppointmentStatus.PAID);
            if (appointment.getQueueNumber() == null || appointment.getQueueNumber() == 0) {
                appointment.setQueueNumber(generateQueueNumber(appointment));
            }
            appointmentRepository.save(appointment);
        } else if (bill.getLabRequest() != null) {
            LabRequest labRequest = bill.getLabRequest();
                labRequest.setStatus(LabRequestStatus.PAID);
                labRequestRepository.save(labRequest);
            }

        bill.setStatus(BillStatus.PAID);
        bill.setPaidAt(LocalDateTime.now());
        return billRepository.save(bill);
    }

    private int generateQueueNumber(Appointment appointment) {
        int doctorId = appointment.getDoctor().getDoctorId();

        Integer maxTodayQueue = appointmentRepository.findTodayMaxQueue(doctorId);
        int nextQueue = (maxTodayQueue != null ? maxTodayQueue : 0) + 1;

        if (nextQueue > 20) {
            throw new IllegalStateException("Doctor has already reached the maximum number of patients (20) for today.");
        }

        return nextQueue;
    }

    public Page<Bill> getBillsByStatus(int status, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);
        return billRepository.findByStatus(status, pageable);
    }

    public Page<Bill> getAllBills(int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);
        return billRepository.findAll(pageable);
    }

    public void exportPdfToResponse(Bill bill, OutputStream outputStream) throws Exception {
        billPdfExporter.export(bill, outputStream);
    }

    // Thống kê doanh thu


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




