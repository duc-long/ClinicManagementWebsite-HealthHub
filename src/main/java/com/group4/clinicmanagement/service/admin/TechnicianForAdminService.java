package com.group4.clinicmanagement.service.admin;

import com.group4.clinicmanagement.dto.admin.PatientDTO;
import com.group4.clinicmanagement.dto.admin.TechnicianDTO;
import com.group4.clinicmanagement.entity.Patient;
import com.group4.clinicmanagement.entity.Role;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.enums.UserStatus;
import com.group4.clinicmanagement.repository.UserRepository;
import com.group4.clinicmanagement.util.PasswordUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TechnicianForAdminService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordUtil passwordUtil;

    public TechnicianForAdminService(UserRepository userRepository, PasswordEncoder passwordEncoder, PasswordUtil passwordUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordUtil = passwordUtil;
    }

    private TechnicianDTO mapToTechnicianDTO(User user) {
        return new TechnicianDTO(
                user.getUserId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getGender(),
                user.getRole().getRoleId(),
                user.getStatus(),
                user.getAvatar());
    }

    @Transactional
    public Page<TechnicianDTO> findAll(Pageable pageable) {
        try {
            Page<User> TechnicianPage = userRepository.findAllByRoleTechnician(pageable);

            List<TechnicianDTO> dtoList = new ArrayList<>();
            for (User Technician : TechnicianPage.getContent()) {
                dtoList.add(mapToTechnicianDTO(Technician));
            }

            return new PageImpl<>(dtoList, pageable, TechnicianPage.getTotalElements());

        } catch (Exception e) {
            System.err.println("Error while fetching patients: " + e.getMessage());

            return Page.empty(pageable);
        }
    }

    @Transactional
    public TechnicianDTO findById(Integer id) {
        User user = userRepository.getUserByUserIdAndRole_RoleId(id, 5).orElseThrow(() -> new RuntimeException("Patient not found"));
        ;
        return mapToTechnicianDTO(user);
    }

    @Transactional
    public Integer newTechnician(TechnicianDTO dto, MultipartFile avatar) {
        User user = new User();
        String encodePassword = passwordEncoder.encode(PasswordUtil.PASSWORD);

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setGender(dto.getGender());
        user.setStatus(dto.getUserStatus());
        user.setUsername(dto.getUsername());
        user.setPasswordHash(encodePassword);
        user.setStatus(UserStatus.ACTIVE);

        Role role = new Role();
        role.setRoleId(5);
        user.setRole(role);

        userRepository.save(user);

        if (avatar != null && !avatar.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/uploads/avatars";
                Files.createDirectories(Paths.get(uploadDir));

                Optional<User> userOpt = userRepository.findUserByUsername(user.getUsername());
                String oldFilename = userOpt.map(User::getAvatar).orElse(null);

                String filename = UUID.randomUUID() + "_" + avatar.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, filename);

                avatar.transferTo(filePath.toFile());

                if (oldFilename != null && !oldFilename.isBlank()) {
                    Path oldFilePath = Paths.get(uploadDir, oldFilename);
                    Files.deleteIfExists(oldFilePath);
                }

                userRepository.updateAvatarFilename(user.getUsername(), filename);

            } catch (IOException e) {
                throw new RuntimeException("Upload avatar failed", e);
            }
        }
        return user.getUserId();
    }

    @Transactional
    public void update(TechnicianDTO dto, MultipartFile avatar) {
        User user = userRepository.getUserByUserIdAndRole_RoleId(dto.getUserId(), 5).orElseThrow(() -> new RuntimeException("Patient not found"));

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setGender(dto.getGender());
        user.setStatus(dto.getUserStatus());

        userRepository.save(user);

        if (avatar != null && !avatar.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/uploads/avatars";
                Files.createDirectories(Paths.get(uploadDir));

                Optional<User> userOpt = userRepository.findUserByUsername(user.getUsername());
                String oldFilename = userOpt.map(User::getAvatar).orElse(null);

                String filename = UUID.randomUUID() + "_" + avatar.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, filename);

                avatar.transferTo(filePath.toFile());

                if (oldFilename != null && !oldFilename.isBlank()) {
                    Path oldFilePath = Paths.get(uploadDir, oldFilename);
                    Files.deleteIfExists(oldFilePath);
                }

                userRepository.updateAvatarFilename(user.getUsername(), filename);

            } catch (IOException e) {
                throw new RuntimeException("Upload avatar failed", e);
            }
        }
    }

    @Transactional
    public void delete(TechnicianDTO dto) {
        User user = userRepository.getUserByUserIdAndRole_RoleId(dto.getUserId(), 5).orElseThrow(() -> new RuntimeException("Patient not found"));
        userRepository.delete(user);
    }
}
