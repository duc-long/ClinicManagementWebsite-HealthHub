package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.entity.Bill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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

    // thong ke doanh thu theo khoang thoi gian
    // Theo NGÀY
    @Query(value = """
        SELECT ISNULL(SUM(total_amount), 0)
        FROM Bill
        WHERE status = 1
          AND CAST(paid_at AS DATE) = :date
    """, nativeQuery = true)
    Double getRevenueByDay(@Param("date") LocalDate date);


    // Theo THÁNG + NĂM
    @Query(value = """
        SELECT ISNULL(SUM(total_amount), 0)
        FROM Bill
        WHERE status = 1
          AND MONTH(paid_at) = :month
          AND YEAR(paid_at) = :year
    """, nativeQuery = true)
    Double getRevenueByMonth(@Param("month") int month, @Param("year") int year);


    // Theo NĂM
    @Query(value = """
        SELECT ISNULL(SUM(total_amount), 0)
        FROM Bill
        WHERE status = 1
          AND YEAR(paid_at) = :year
    """, nativeQuery = true)
    Double getRevenueByYear(@Param("year") int year);

    @Query(value = """
    SELECT DATEPART(hour, paid_at) AS label, SUM(total_amount) AS total
    FROM Bill
    WHERE paid_at >= :start AND paid_at <= :end
    GROUP BY DATEPART(hour, paid_at)
    ORDER BY label
""", nativeQuery = true)
    List<Object[]> getRevenueByDay(LocalDateTime start, LocalDateTime end);

    @Query(value = """
    SELECT DATEPART(day, paid_at) AS label, SUM(total_amount) AS total
    FROM Bill
    WHERE paid_at >= :start AND paid_at <= :end
    GROUP BY DATEPART(day, paid_at)
    ORDER BY label
""", nativeQuery = true)
    List<Object[]> getRevenueByMonth(LocalDateTime start, LocalDateTime end);

    @Query(value = """
    SELECT DATEPART(month, paid_at) AS label, SUM(total_amount) AS total
    FROM Bill
    WHERE paid_at >= :start AND paid_at <= :end
    GROUP BY DATEPART(month, paid_at)
    ORDER BY label
""", nativeQuery = true)
    List<Object[]> getRevenueByYear(LocalDateTime start, LocalDateTime end);

}
