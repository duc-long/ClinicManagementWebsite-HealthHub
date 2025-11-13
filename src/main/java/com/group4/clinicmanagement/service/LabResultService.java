package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.LabResultDTO;
import com.group4.clinicmanagement.dto.doctor.LabImageDTO;
import com.group4.clinicmanagement.entity.LabImage;
import com.group4.clinicmanagement.entity.LabRequest;
import com.group4.clinicmanagement.entity.LabResult;
import com.group4.clinicmanagement.entity.Staff;
import com.group4.clinicmanagement.enums.LabRequestStatus;
import com.group4.clinicmanagement.repository.LabImageRepository;
import com.group4.clinicmanagement.repository.LabRequestRepository;
import com.group4.clinicmanagement.repository.LabResultRepository;
import com.group4.clinicmanagement.repository.StaffRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LabResultService {

    private final LabResultRepository labResultRepository;
    private final LabRequestRepository labRequestRepository;
    private final StaffRepository staffRepository;
    private final LabImageRepository labImageRepository;

    public LabResultService(LabResultRepository labResultRepository,
                            LabRequestRepository labRequestRepository,
                            StaffRepository staffRepository,
                            LabImageRepository labImageRepository) {
        this.labResultRepository = labResultRepository;
        this.labRequestRepository = labRequestRepository;
        this.staffRepository = staffRepository;
        this.labImageRepository = labImageRepository;
    }

    @Transactional
    public Integer createResultForRequest(Integer labRequestId, int technicianUserId) {
        LabRequest request = labRequestRepository.findById(labRequestId)
                .orElseThrow(() -> new RuntimeException("LabRequest not found"));

        Staff technician = staffRepository.getReferenceByStaffId(technicianUserId);
        if (technician == null) {
            throw new RuntimeException("Technician not found for userId " + technicianUserId);
        }

        LabResult result = new LabResult();
        result.setLabRequest(request);
        result.setCreatedAt(LocalDateTime.now());
        result.setTechnician(technician);

        request.setStatus(LabRequestStatus.RUNNING);

        labRequestRepository.save(request);
        labResultRepository.save(result);

        return result.getResultId();
    }


    public List<LabResultDTO> findLabResultList() {
        return labResultRepository.findAllByLabRequestStatus(List.of(4,2))
        .stream()
                .map(LabResultDTO::fromEntity)
                .toList();
    }

    public List<LabResultDTO> filterResults(String resultId, String testName, boolean isAll) {
        List<LabResultDTO> resultDTOSs = new ArrayList<>();
        List<LabResult> labResults = labResultRepository.filterResults(resultId, testName);

        if (isAll) {
            return labResults.stream().map(LabResultDTO::fromEntity).toList();
        } else {
            for (LabResult labResult : labResults) {
                if (LocalDate.now().isEqual(labResult.getCreatedAt().toLocalDate())) {
                    resultDTOSs.add(LabResultDTO.fromEntity(labResult));
                }
            }
            return resultDTOSs;
        }
    }

    public LabResultDTO findById(Integer id) {
        LabResult result = labResultRepository.findById(id)
                .orElse(null);
        if (result == null) {
            return null;
        }
        return LabResultDTO.fromEntity(result);
    }

    @Transactional
    public void confirmResult(int resultId) {
        LabResult result = labResultRepository.findById(resultId)
                .orElseThrow(() -> new RuntimeException("Result not found"));

        LabRequest request = result.getLabRequest();
        if (request != null) {
            request.setStatus(LabRequestStatus.COMPLETED);
            labRequestRepository.save(request);
        }
    }


    private String saveFile(MultipartFile file, String folder) throws IOException {
        Path uploadDir = Paths.get("uploads", folder);
        if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadDir.resolve(fileName);

        // Lưu file thật
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Trả về đường dẫn tương đối để lưu DB
        return fileName;
    }

    private void deleteFileIfExists(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) return;

        try {
            Path filePath = Paths.get("uploads", "labs", relativePath);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("⚠️ Failed to delete file: " + relativePath);
        }
    }

    @Transactional
    public void updateResultWithImages(
            LabResultDTO dto,
            List<MultipartFile> xrayFiles,
            List<Integer> deleteImageIds,
            List<Integer> imageIds,
            List<String> imageDescriptions,
            List<String> newDescriptions) throws IOException {

        LabResult result = labResultRepository.findById(dto.getResultId())
                .orElseThrow(() -> new RuntimeException("Result not found"));

        result.setResultText(dto.getResultText());
        labResultRepository.save(result);

        if (imageIds != null && imageDescriptions != null) {
            for (int i = 0; i < imageIds.size(); i++) {
                Integer imgId = imageIds.get(i);
                String newDesc = imageDescriptions.get(i);
                labImageRepository.findById(imgId).ifPresent(img -> {
                    img.setDescription(newDesc);
                    labImageRepository.save(img);
                });
            }
        }

        if (deleteImageIds != null) {
            for (Integer imgId : deleteImageIds) {
                labImageRepository.findById(imgId).ifPresent(img -> {
                    deleteFileIfExists(img.getFilePath());
                    labImageRepository.deleteLabImageByFilePath(img.getFilePath());
                });
            }
        }

        if (xrayFiles != null && !xrayFiles.isEmpty()) {
            for (int i = 0; i < xrayFiles.size(); i++) {
                MultipartFile file = xrayFiles.get(i);
                if (!file.isEmpty()) {
                    String filePath = saveFile(file, "labs");
                    LabImage newImg = new LabImage();
                    newImg.setLabResult(result);
                    newImg.setFilePath(filePath);

                    if (newDescriptions != null && i < newDescriptions.size()) {
                        newImg.setDescription(newDescriptions.get(i));
                    } else {
                        newImg.setDescription("Uploaded: " + file.getOriginalFilename());
                    }

                    labImageRepository.save(newImg);
                }
            }
        }
    }

    @Transactional
    public void deleteResult(int resultId) {
        labResultRepository.deleteById(resultId);
    }

    // method to get lab result DTO
    public com.group4.clinicmanagement.dto.doctor.LabResultDTO getResultByLabRequestId(Integer labRequestId) {
        LabResult result = labResultRepository.findByLabRequestId(labRequestId).orElse(null);
        if (result == null) {
            return null;
        }

        com.group4.clinicmanagement.dto.doctor.LabResultDTO dto = new com.group4.clinicmanagement.dto.doctor.LabResultDTO();
        dto.setResultId(result.getResultId());
        dto.setLabRequestId(result.getLabRequest().getLabRequestId());
        dto.setTechnicianName(result.getTechnician().getFullName());
        dto.setResultText(result.getResultText());
        dto.setCreatedAt(result.getCreatedAt());

        // map images
        List<LabImageDTO> imageDTOs = result.getImages()
                .stream()
                .map(img -> new LabImageDTO(
                        img.getImageId(),
                        img.getFilePath(),
                        img.getDescription(),
                        img.getCreatedAt()
                ))
                .toList();
        dto.setImages(imageDTOs);
        return dto;
    }
}
