package com.group4.clinicmanagement.dto.doctor;

import com.group4.clinicmanagement.enums.PrescriptionStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class PrescriptionDTO {
    private int prescriptionId;
    private int recordId;
    private int doctorId;
    private String doctorName;
    private PrescriptionStatus status;
    private List<PrescriptionDetailDTO> prescriptionDetails = new ArrayList<>();
}
