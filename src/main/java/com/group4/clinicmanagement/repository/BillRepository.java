package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.entity.Bill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Integer> {
    Bill findByAppointment_AppointmentId(Integer appointmentId);

    boolean existsByAppointment_AppointmentId(Integer appointmentId);


    Optional<Bill> findByLabRequest_LabRequestId(Integer labRequestId);


    boolean existsByLabRequest_LabRequestId(Integer labRequestId);

    @Query("SELECT b FROM Bill b " +
            "LEFT JOIN FETCH b.appointment a " +
            "LEFT JOIN FETCH a.patient p " +
            "LEFT JOIN FETCH b.labRequest l " +
            "LEFT JOIN FETCH l.medicalRecord mr " +
            "LEFT JOIN FETCH mr.patient mp " +
            "WHERE b.statusValue = :status")
    Page<Bill> findByStatus(@Param("status") int status, Pageable pageable);

}
