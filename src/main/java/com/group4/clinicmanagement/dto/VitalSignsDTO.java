package com.group4.clinicmanagement.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class VitalSignsDTO {
    private int vitalId;
    private int recordId;    // ID của MedicalRecord
    private int nurseId;     // ID của y tá (User)
    private String nurseName;    // thêm để hiển thị tên y tá nếu cần
    private int systolic;     // tạm để nhập số trên
    private int diastolic;    // tạm để nhập số dưới
    private double heightCm;
    private double weightKg;
    private String bloodPressure;
    private int heartRate;
    private double temperature;
    private LocalDateTime recordedAt;
}
