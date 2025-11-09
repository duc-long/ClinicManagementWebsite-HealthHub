package com.group4.clinicmanagement.dto;

import com.group4.clinicmanagement.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecepCashAppointmentDTO {
    private int appointmentId;
    private int patientId;
    private String doctorName;
    private String patientName;
    private String receptionistName;
    private String phonePatient;
    private LocalDate appointmentDate;
    private LocalDateTime createdAt;

    private AppointmentStatus status;
    private int queueNumber;
    private String notes;
    private String cancelReason;

    //use to check if status confirm and appointment date equal date now
    private boolean canCheck;

    private Integer billId;
}
