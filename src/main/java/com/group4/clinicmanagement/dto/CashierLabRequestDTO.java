package com.group4.clinicmanagement.dto;

import com.group4.clinicmanagement.enums.LabRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CashierLabRequestDTO {
    private Integer labRequestId;
    private String patientName;
    private String doctorName;
    private String testName;
    private Double cost;
    private LocalDateTime requestedAt;
    private LabRequestStatus status;
    private Integer billId;

    
    private boolean canCreateBill;
}
