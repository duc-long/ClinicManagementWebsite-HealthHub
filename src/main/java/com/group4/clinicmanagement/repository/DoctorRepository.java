package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.entity.Doctor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {

    Doctor getDoctorByDoctorId(Integer doctorId);

    @Query("""
           SELECT d FROM Doctor d
           WHERE d.department.departmentId =:departmentId AND d.doctorId NOT IN (
               SELECT s.doctor.doctorId
               FROM DoctorDailySlot s
               WHERE s.slotDate = :date
               AND s.availableSlots <= 0
           )
           """)
    List<Doctor> findAvailableDoctors(
            @Param("departmentId") int departmentId,
            @Param("date") LocalDate date);

    @Query("""
            SELECT d FROM Doctor d
            WHERE d.staff.statusValue = :#{T(com.group4.clinicmanagement.enums.UserStatus).ACTIVE.value}
              AND d.profileVisibility = true
            """)
    List<Doctor> findAllVisibleAndActiveDoctors();


    @Query("""
            SELECT d FROM Doctor d
            WHERE LOWER(d.staff.fullName) LIKE LOWER(CONCAT('%', :name, '%'))
              AND (:departmentId = 0 OR d.department.departmentId = :departmentId)
              AND d.staff.statusValue = :#{T(com.group4.clinicmanagement.enums.UserStatus).ACTIVE.value}
              AND d.profileVisibility = true""")
    List<Doctor> findByNameAndDepartmentId(@Param("name") String name, @Param("departmentId") Integer departmentId);


    @Query("""
            SELECT d FROM Doctor d
            WHERE d.doctorId = :id
              AND d.staff.statusValue = :#{T(com.group4.clinicmanagement.enums.UserStatus).ACTIVE.value}
              AND d.profileVisibility = true""")
    Optional<Doctor> findVisibleActiveDoctorById(@Param("id") Integer id);

    @Query("""
            SELECT d FROM Doctor d
            WHERE d.department.name = :departmentName
              AND d.staff.statusValue = :#{T(com.group4.clinicmanagement.enums.UserStatus).ACTIVE.value}
              AND d.profileVisibility = true""")
    List<Doctor> findVisibleActiveDoctorsByDepartment(@Param("departmentName") String departmentName);

    @Query("""
            SELECT d FROM Doctor d
            WHERE d.staff.statusValue = :#{T(com.group4.clinicmanagement.enums.UserStatus).ACTIVE.value} AND d.profileVisibility = true
            ORDER BY d.doctorId DESC""")
    List<Doctor> findTopDoctors(Pageable pageable);
}
