package com.group4.clinicmanagement.dto;

import com.group4.clinicmanagement.entity.LabImage;
import com.group4.clinicmanagement.entity.LabRequest;
import com.group4.clinicmanagement.entity.LabResult;
import com.group4.clinicmanagement.entity.Staff;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabResultDTO {

    private Integer resultId;
    private Integer labRequestId;

    private String testName;
    private String patientName;
    private String technicianName;
    @NotBlank
    private String resultText;

    private List<LabImage> images;
    private LocalDateTime createdAt;
    private String labRequestStatus;

    private List<Integer> deleteImageIds;
    private List<MultipartFile> xrayFiles;


    public static LabResultDTO fromEntity(LabResult entity) {
        if (entity == null) return null;

        LabResultDTO dto = new LabResultDTO();
        dto.setResultId(entity.getResultId());
        dto.setResultText(entity.getResultText());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setImages(entity.getImages());

        LabRequest request = entity.getLabRequest();
        if (request != null) {
            dto.setLabRequestId(request.getLabRequestId());
            dto.setTestName(request.getTest() != null ? request.getTest().getName() : null);
            dto.setLabRequestStatus(request.getStatus().name());

            if (request.getMedicalRecord() != null &&
                    request.getMedicalRecord().getPatient() != null &&
                    request.getMedicalRecord().getPatient() != null) {

                dto.setPatientName(request.getMedicalRecord().getPatient().getFullName());
            }
        }

        Staff tech = entity.getTechnician();
        if (tech != null) {
            dto.setTechnicianName(tech.getFullName());
        }

        return dto;
    }

    public LabResult toEntity(LabRequest labRequest, Staff technician) {
        LabResult entity = new LabResult();
        entity.setResultId(this.resultId);
        entity.setLabRequest(labRequest);
        entity.setTechnician(technician);
        entity.setResultText(this.resultText);
        entity.setCreatedAt(this.createdAt != null ? this.createdAt : LocalDateTime.now());
        entity.setImages(this.images);
        return entity;
    }
}
