package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.DoctorDTO;
import com.group4.clinicmanagement.dto.DoctorHomeDTO;
import com.group4.clinicmanagement.dto.ReceptionistUserDTO;
import com.group4.clinicmanagement.entity.Department;
import com.group4.clinicmanagement.entity.Doctor;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.repository.DepartmentRepository;
import com.group4.clinicmanagement.repository.DoctorRepository;
import com.group4.clinicmanagement.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DoctorService {

    private DoctorRepository doctorRepository;
    private DepartmentRepository departmentRepository;
    private UserRepository userRepository;


    public DoctorService(DoctorRepository doctorRepository, DepartmentRepository departmentRepository,
                         UserRepository userRepository) {
        this.doctorRepository = doctorRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
    }

    public List<Doctor> findAllDoctors() {
        return doctorRepository.findAll();
    }

    @Transactional
    public DoctorHomeDTO toDTO(Doctor doctor) {
        User user = doctor.getUser();
        Department dept = doctor.getDepartment();

        return DoctorHomeDTO.builder()
                .doctorId(doctor.getDoctorId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .gender(user.getGender())
                .licenseNo(doctor.getLicenseNo())
                .specialty(doctor.getSpecialty())
                .degree(doctor.getDegree())
                .yearsExperience(doctor.getYearsExperience())
                .bio(doctor.getBio())
                .profileVisibility(Boolean.TRUE.equals(doctor.getProfileVisibility()))
                .departmentName(dept != null ? dept.getName() : null)
                .avatarFilename(user.getAvatar())
                .build();
    }

    @Transactional
    public List<DoctorHomeDTO> findAllVisibleAndActiveDoctors() {
        List<Doctor> doctors = doctorRepository.findAllVisibleAndActiveDoctors();
        List<DoctorHomeDTO> result = new ArrayList<>();

        for (Doctor doctor : doctors) {
            DoctorHomeDTO dto = toDTO(doctor);
            result.add(dto);
        }
        return result;
    }

    @Transactional
    public List<DoctorHomeDTO> findByNameAndDepartmentId(String name, Integer departmentId) {
        List<Doctor> doctors = doctorRepository.findByNameAndDepartmentId(name, departmentId);
        List<DoctorHomeDTO> result = new ArrayList<>();

        for (Doctor doctor : doctors) {
            DoctorHomeDTO dto = toDTO(doctor);
            result.add(dto);
        }
        return result;
    }

    @Transactional
    public DoctorHomeDTO findVisibleActiveDoctorById(Integer id) {
        Doctor doctor = doctorRepository.findVisibleActiveDoctorById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));
        return toDTO(doctor);
    }

    @Transactional
    public List<DoctorHomeDTO> findVisibleActiveDoctorsByDepartment(String departmentName) {
        List<Doctor> doctors = doctorRepository.findVisibleActiveDoctorsByDepartment(departmentName);
        List<DoctorHomeDTO> result = new ArrayList<>();
        for (Doctor doctor : doctors) {
            DoctorHomeDTO dto = toDTO(doctor);
            result.add(dto);
        }
        return result;
    }

    @Transactional
    public List<DoctorHomeDTO> findTopDoctors(Pageable pageable) {
        List<Doctor> topDoctors = doctorRepository.findTopDoctors(pageable);
        List<DoctorHomeDTO> result = new ArrayList<>();
        for (Doctor doctor : topDoctors) {
            DoctorHomeDTO dto = toDTO(doctor);
            result.add(dto);
        }
        return result;
    }

    public Doctor findDoctorById(int id) {
        return doctorRepository.getDoctorByDoctorId(id);
    }


    public Doctor findByDoctorId(Integer doctorId) {
        return doctorRepository.findById(doctorId).get();
    }

    public Department findDoctorDepartment(int departmentId) {
        Department department = departmentRepository.findByDepartmentId(departmentId)
                .orElse(null);

        return department;
    }

    @Transactional
    public void saveDoctor(Doctor doctor) {
        doctorRepository.save(doctor);
    }

    @Transactional
    public void savePatientUserWithAvatar(MultipartFile avatar, String username) {

        if (avatar != null && !avatar.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/uploads/avatars/doctor";
                Files.createDirectories(Paths.get(uploadDir));

                Optional<User> userOpt = userRepository.findUserByUsername(username);
                String oldFilename = userOpt.map(User::getAvatar).orElse(null);

                String filename = UUID.randomUUID() + "_" + avatar.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, filename);

                avatar.transferTo(filePath.toFile());

                if (oldFilename != null && !oldFilename.isBlank()) {
                    Path oldFilePath = Paths.get(uploadDir, oldFilename);
                    Files.deleteIfExists(oldFilePath);
                }

                userRepository.updateAvatarFilename(username, filename);

            } catch (IOException e) {
                throw new RuntimeException("Upload avatar failed", e);
            }
        }
    }

    // method to check valid doctor information
    @Transactional(readOnly = true)
    public Optional<String> validateUpdateDoctorInfo(DoctorDTO doctorDTO, int currentUserId) {
        if (doctorDTO == null) {
            return Optional.of("Invalid request.");
        }

        // Basic null/blank checks
        if (doctorDTO.getFullName() == null || doctorDTO.getFullName().trim().isEmpty()) {
            return Optional.of("Full name is required.");
        }
        if (doctorDTO.getGender() == null) {
            return Optional.of("Gender is required.");
        }
        if (doctorDTO.getPhone() == null || doctorDTO.getPhone().trim().isEmpty()) {
            return Optional.of("Phone number is required.");
        }
        if (doctorDTO.getEmail() == null || doctorDTO.getEmail().trim().isEmpty()) {
            return Optional.of("Email is required.");
        }

        String email = doctorDTO.getEmail().trim();
        String phone = doctorDTO.getPhone().trim();

        // Format checks
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        String phoneRegex = "^(?:\\+84|0084|0)(3[2-9]|5[6|8|9]|7[0|6-9]|8[0-6|8|9]|9[0-4|6-9])[0-9]{7}$";
        if (!email.matches(emailRegex)) {
            return Optional.of("Invalid email format.");
        }

        // phone: allow digits only, length 8-15
        if (!phone.matches(phoneRegex)) {
            return Optional.of("Phone number must contain 8 to 15 digits (digits only).");
        }

        if (userRepository.existsByEmailAndUserIdNot(email, doctorDTO.getDoctorId())) {
            return Optional.of("Email is already in use by another account.");
        }

        if (userRepository.existsByPhoneAndUserIdNot(phone, doctorDTO.getDoctorId())) {
            return Optional.of("Phone number is already in use by another account.");
        }

        // All checks passed
        return Optional.empty();
    }

    // method to update doctor avatar
    @Transactional
    public void updateDoctorProfile(String doctorUsername, MultipartFile avatarFile) {
        User user = userRepository.findByUsername(doctorUsername).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("Người dùng không tồn tại.");
        }

        // Áp DTO vào entity (bạn viết method này giống applyDTOToEntity của receptionist)

        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                String contentType = avatarFile.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new IllegalArgumentException("Invalid file type. Only image files are allowed.");
                }

                String originalFilename = avatarFile.getOriginalFilename();
                if (originalFilename == null) {
                    throw new IllegalArgumentException("Invalid file name.");
                }
                String lower = originalFilename.toLowerCase();
                if (!lower.endsWith(".jpg") &&
                        !lower.endsWith(".jpeg") &&
                        !lower.endsWith(".png") &&
                        !lower.endsWith(".gif") &&
                        !lower.endsWith(".webp")) {
                    throw new IllegalArgumentException("Unsupported image format. Please upload JPG, PNG, GIF, or WEBP files.");
                }

                Path uploadDir = Paths.get("uploads/avatars");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                String fileName = "doctor_" + UUID.randomUUID() + "_" + originalFilename.replaceAll("\\s+", "_");
                Path filePath = uploadDir.resolve(fileName).normalize();

                // Lưu file (ghi đè nếu tồn tại)
                Files.copy(avatarFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Xóa avatar cũ nếu có (tên file lưu trong user.getAvatar())
                try {
                    String oldAvatar = user.getAvatar();
                    if (oldAvatar != null && !oldAvatar.isEmpty()) {
                        Path oldFile = uploadDir.resolve(oldAvatar).normalize();
                        if (Files.exists(oldFile)) {
                            Files.delete(oldFile);
                        }
                    }
                } catch (Exception ignore) {
                    // nếu xóa fail thì ko block quá trình
                }

                // Lưu tên file (hoặc đường dẫn tuỳ bạn). Ví dụ lưu filename:
                user.setAvatar(fileName);

            } catch (IllegalArgumentException e) {
                throw e; // để controller bắt và hiển thị lỗi validate
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload avatar: " + e.getMessage(), e);
            }
        }

        // Lưu user vào repository
        userRepository.save(user);
    }

}
