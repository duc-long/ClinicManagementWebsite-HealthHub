package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.entity.Bill;
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

    @Query("""
        SELECT b FROM Bill b
        WHERE b.labRequest.labRequestId = :labRequestId
    """)
    Optional<Bill> findByLabRequest_LabRequestId(@Param("labRequestId") Integer labRequestId);
}
