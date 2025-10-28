package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.entity.Patient;
import com.group4.clinicmanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
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

    Optional<User> findUserByUsername(String username);

    Optional<User> findByUsername(String username);

    Optional<User> findByUserId(int userId);

    @Modifying
    @Query("UPDATE User u SET u.avatar = :filename WHERE u.username = :username")
    void updateAvatarFilename(@Param("username") String username,
                              @Param("filename") String filename);


    @Query("SELECT u FROM User u WHERE u.email = :email AND u.userId <> :userId")
    Optional<User> findOtherByEmail(@Param("email") String email, @Param("userId") Integer userId);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Query(value = "SELECT MAX(user_id) FROM Users", nativeQuery = true)
    Optional<Integer> getMaxUserId();


    Optional<User> findByFullName(String fullname);


    User getReferenceByUserId(Integer userId);


    @Query("SELECT u FROM User u WHERE u.role.roleId = 3")
    Page<User> findAllByRoleReceptionist(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role.roleId = 4")
    Page<User> findAllByRoleCashier(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role.roleId = 5")
    Page<User> findAllByRoleTechnician(Pageable pageable);

    Optional<User> getUserByUserIdAndRole_RoleId(Integer userId, int roleRoleId);


}
