package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.fullName = :fullName, u.email = :email, " +
            "u.phone = :phone, u.genderValue = :gender, u.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE u.username = :username")
    int updateProfileByUsername(@Param("username") String username,
                                @Param("fullName") String fullName,
                                @Param("email") String email,
                                @Param("phone") String phone,
                                @Param("gender") Integer gender);

    Optional<Object> findUserByUsername(String username);

}
