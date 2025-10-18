package com.group4.clinicmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRecordDetailDTO {
    //UserPatient
    private String fullName;
    private String email;
    private String phone;
    private Integer genderValue;

    //Patient
    private LocalDate dateOfBirth;
    private String address;

    //UserDoctor
    private String doctorFullName;

    //Doctor
    private String specialty;

    //VitalSign
    private Double heightCm;
    private Double weightKg;
    private String bloodPressure;
    private Integer heartRate;
    private Double temperature;

    //LabRequest


    //Prescription

    //MedicalRecord
    private String diagnosis;
    private String notes;
    private LocalDateTime createdAt;
}