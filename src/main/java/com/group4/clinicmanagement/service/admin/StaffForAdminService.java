package com.group4.clinicmanagement.service.admin;

import com.group4.clinicmanagement.dto.admin.StaffDTO;
import com.group4.clinicmanagement.entity.Role;
import com.group4.clinicmanagement.entity.Staff;
import com.group4.clinicmanagement.enums.UserStatus;
import com.group4.clinicmanagement.repository.StaffRepository;
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
public class StaffForAdminService {
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordUtil passwordUtil;

    public StaffForAdminService(StaffRepository staffRepository, PasswordEncoder passwordEncoder, PasswordUtil passwordUtil) {
        this.staffRepository = staffRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordUtil = passwordUtil;
    }


    private StaffDTO mapToStaffDTO(Staff staff) {
        return new StaffDTO(
                staff.getStaffId(),
                staff.getUsername(),
                staff.getFullName(),
                staff.getEmail(),
                staff.getPhone(),
                staff.getGender(),
                staff.getRole().getRoleId(),
                staff.getStatus(),
                staff.getAvatar());
    }

    @Transactional
    public Page<StaffDTO> findAll(Pageable pageable, Integer roleId) {
        try {
            Page<Staff> StaffPage = staffRepository.findAllByRole(pageable, roleId);

            List<StaffDTO> dtoList = new ArrayList<>();
            for (Staff Staff : StaffPage.getContent()) {
                dtoList.add(mapToStaffDTO(Staff));
            }

            return new PageImpl<>(dtoList, pageable, StaffPage.getTotalElements());

        } catch (Exception e) {
            System.err.println("Error while fetching patients: " + e.getMessage());

            return Page.empty(pageable);
        }
    }

    @Transactional
    public StaffDTO findById(Integer id, Integer role) {
        Staff user = staffRepository.getStaffByStaffIdAndRole_RoleId(id, role).orElseThrow(() -> new RuntimeException("Patient not found"));
        ;
        return mapToStaffDTO(user);
    }

    @Transactional
    public Integer newStaff(StaffDTO dto, MultipartFile avatar, Integer roleId) {
        Staff user = new Staff();
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
        role.setRoleId(roleId);
        user.setRole(role);

        staffRepository.save(user);

        if (avatar != null && !avatar.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/uploads/avatars";
                Files.createDirectories(Paths.get(uploadDir));

                Optional<Staff> userOpt = staffRepository.findStaffByUsername(user.getUsername());
                String oldFilename = userOpt.map(Staff::getAvatar).orElse(null);

                String filename = UUID.randomUUID() + "_" + avatar.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, filename);

                avatar.transferTo(filePath.toFile());

                if (oldFilename != null && !oldFilename.isBlank()) {
                    Path oldFilePath = Paths.get(uploadDir, oldFilename);
                    Files.deleteIfExists(oldFilePath);
                }

                staffRepository.updateAvatarFilename(user.getUsername(), filename);

            } catch (IOException e) {
                throw new RuntimeException("Upload avatar failed", e);
            }
        }
        return user.getStaffId();
    }

    @Transactional
    public void update(StaffDTO dto, MultipartFile avatar, Integer staffId) {
        Staff user = staffRepository.getStaffByStaffIdAndRole_RoleId(dto.getUserId(), staffId).orElseThrow(() -> new RuntimeException("Patient not found"));

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setGender(dto.getGender());
        user.setStatus(dto.getUserStatus());

        staffRepository.save(user);

        if (avatar != null && !avatar.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/uploads/avatars";
                Files.createDirectories(Paths.get(uploadDir));

                Optional<Staff> userOpt = staffRepository.findStaffByUsername(user.getUsername());
                String oldFilename = userOpt.map(Staff::getAvatar).orElse(null);

                String filename = UUID.randomUUID() + "_" + avatar.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, filename);

                avatar.transferTo(filePath.toFile());

                if (oldFilename != null && !oldFilename.isBlank()) {
                    Path oldFilePath = Paths.get(uploadDir, oldFilename);
                    Files.deleteIfExists(oldFilePath);
                }

                staffRepository.updateAvatarFilename(user.getUsername(), filename);

            } catch (IOException e) {
                throw new RuntimeException("Upload avatar failed", e);
            }
        }
    }

    @Transactional
    public void delete(StaffDTO dto, Integer roleId) {
        Staff user = staffRepository.getStaffByStaffIdAndRole_RoleId(dto.getUserId(), roleId).orElseThrow(() -> new RuntimeException("Patient not found"));
        staffRepository.delete(user);
    }

    public boolean isUsernameDuplicate(String username) {
        return staffRepository.findByUsername(username).isPresent();
    }


    public boolean isMailNoDuplicateForUpd(String mail, Integer id) {
        Staff user = staffRepository.findByStaffId(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getEmail().equals(mail)) {
            return false;
        } else {
            return staffRepository.findUserByEmail(mail).isPresent();
        }
    }

    public boolean isMailNoDuplicateForNew(String mail) {
        return staffRepository.findUserByEmail(mail).isPresent();
    }

    public boolean isPhoneNoDuplicateForUpd(String phone, Integer id) {
        Staff user = staffRepository.findByStaffId(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getPhone().equals(phone)) {
            return false;
        } else {
            return staffRepository.findStaffByPhone(phone).isPresent();
        }
    }

    public boolean isPhoneNoDuplicateForNew(String phone) {
        return staffRepository.findStaffByPhone(phone).isPresent();
    }

}
