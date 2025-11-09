package com.group4.clinicmanagement.repository.admin;

import com.group4.clinicmanagement.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BillForAdminRepository extends JpaRepository<Bill, Integer> {

    // Theo NGÀY
    @Query(value = """
        SELECT ISNULL(SUM(amount), 0)
        FROM Bill
        WHERE status = 1
          AND CAST(paid_at AS DATE) = :date
    """, nativeQuery = true)
    Double getRevenueByDay(@Param("date") LocalDate date);


    // Theo THÁNG + NĂM
    @Query(value = """
        SELECT ISNULL(SUM(amount), 0)
        FROM Bill
        WHERE status = 1
          AND MONTH(paid_at) = :month
          AND YEAR(paid_at) = :year
    """, nativeQuery = true)
    Double getRevenueByMonth(@Param("month") int month, @Param("year") int year);


    // Theo NĂM
    @Query(value = """
        SELECT ISNULL(SUM(amount), 0)
        FROM Bill
        WHERE status = 1
          AND YEAR(paid_at) = :year
    """, nativeQuery = true)
    Double getRevenueByYear(@Param("year") int year);

    @Query(value = """
    SELECT DATEPART(hour, paid_at) AS label, SUM(amount) AS total
    FROM Bill
    WHERE paid_at >= :start AND paid_at <= :end
    GROUP BY DATEPART(hour, paid_at)
    ORDER BY label
""", nativeQuery = true)
    List<Object[]> getRevenueByDay(LocalDateTime start, LocalDateTime end);

    @Query(value = """
    SELECT DATEPART(day, paid_at) AS label, SUM(amount) AS total
    FROM Bill
    WHERE paid_at >= :start AND paid_at <= :end
    GROUP BY DATEPART(day, paid_at)
    ORDER BY label
""", nativeQuery = true)
    List<Object[]> getRevenueByMonth(LocalDateTime start, LocalDateTime end);

    @Query(value = """
    SELECT DATEPART(month, paid_at) AS label, SUM(amount) AS total
    FROM Bill
    WHERE paid_at >= :start AND paid_at <= :end
    GROUP BY DATEPART(month, paid_at)
    ORDER BY label
""", nativeQuery = true)
    List<Object[]> getRevenueByYear(LocalDateTime start, LocalDateTime end);

}

