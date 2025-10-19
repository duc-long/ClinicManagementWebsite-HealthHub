package com.group4.clinicmanagement.dto;

import com.group4.clinicmanagement.enums.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AppointmentDTO {
    private Integer appointmentId;

    private String doctorName;        // a.getDoctor().getUser().getFullName()
    private String patientName;       // a.getPatient().getUser().getFullName()
    private String receptionistName;  // a.getReceptionist().getFullName()

    private LocalDate appointmentDate;
    private LocalDateTime createdAt;

    private AppointmentStatus status;
    private Integer queueNumber;

    private String notes;
    private String cancelReason;
}
