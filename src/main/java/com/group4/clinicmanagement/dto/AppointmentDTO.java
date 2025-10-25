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
public class AppointmentDTO {
    private int appointmentId;

    private String doctorName;        // a.getDoctor().getUser().getFullName()
    private String patientName;       // a.getPatient().getUser().getFullName()
    private String receptionistName;  // a.getReceptionist().getFullName()

    private LocalDate appointmentDate;
    private LocalDateTime createdAt;

    private AppointmentStatus status;
    private int queueNumber;
    private String notes;
    private String cancelReason;

}
