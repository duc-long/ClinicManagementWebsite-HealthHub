package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Integer> {
    @Modifying
    @Transactional
    @Query("UPDATE Staff u SET u.fullName = :fullName, u.email = :email, " +
            "u.phone = :phone, u.genderValue = :gender, u.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE u.username = :username")
    int updateProfileByUsername(@Param("username") String username,
                                @Param("fullName") String fullName,
                                @Param("email") String email,
                                @Param("phone") String phone,
                                @Param("gender") Integer gender);

    Optional<Staff> findStaffByUsername(String username);

    Optional<Staff> findByUsername(String username);

    Optional<Staff> findByStaffId(int userId);

    @Modifying
    @Query("UPDATE Staff u SET u.avatar = :filename WHERE u.username = :username")
    void updateAvatarFilename(@Param("username") String username,
                              @Param("filename") String filename);


    @Query("SELECT u FROM Staff u WHERE u.email = :email AND u.staffId <> :userId")
    Optional<Staff> findOtherByEmail(@Param("email") String email, @Param("userId") Integer userId);

    Optional<Staff> findByEmail(String email);

    boolean existsByUsername(String username);


    Staff getReferenceByStaffId(Integer userId);


    Staff findByUsernameIgnoreCase(String username);

    @Query("SELECT u FROM Staff u WHERE u.username = :username AND u.role.roleId = :roleId")
    Staff findByUsernameAndRoleId(@Param("username") String username, @Param("roleId") Integer roleId);
    Optional<Object> findUserByEmail(String email);
}
