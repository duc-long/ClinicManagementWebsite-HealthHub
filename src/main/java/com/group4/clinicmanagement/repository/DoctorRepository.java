package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.entity.Doctor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {

    @Query("""
    SELECT d FROM Doctor d
    WHERE d.user.isActive = true
      AND d.profileVisibility = true""")
    List<Doctor> findAllVisibleAndActiveDoctors();


    @Query("""
    SELECT d FROM Doctor d
    WHERE LOWER(d.user.fullName) LIKE LOWER(CONCAT('%', :name, '%'))
      AND (:departmentId = 0 OR d.department.departmentId = :departmentId)
      AND d.user.isActive = true
      AND d.profileVisibility = true""")
    List<Doctor> findByNameContainingIgnoreCaseAndDepartmentId(
            @Param("name") String name,
            @Param("departmentId") Integer departmentId);


    @Query("""
    SELECT d FROM Doctor d
    WHERE d.doctorId = :id
      AND d.user.isActive = true
      AND d.profileVisibility = true""")
    Optional<Doctor> findVisibleActiveDoctorById(@Param("id") Integer id);

    @Query("""
    SELECT d FROM Doctor d
    WHERE d.department.name = :departmentName
      AND d.user.isActive = true
      AND d.profileVisibility = true""")
    List<Doctor> findVisibleActiveDoctorsByDepartment(@Param("departmentName") String departmentName);

    @Query("""
    SELECT d FROM Doctor d
    WHERE d.user.isActive = true AND d.profileVisibility = true
    ORDER BY d.createdAt DESC
""")
    List<Doctor> findTopDoctors(Pageable pageable);
}
