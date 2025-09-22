package com.example.football_field_management.repository;

import com.example.football_field_management.dto.MonthlyRevenueDTO;
import com.example.football_field_management.model.OrderPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderPaymentRepository extends JpaRepository<OrderPayment, Long> {

    @Query("""
       SELECT FUNCTION('MONTH', o.paidAt),
              SUM(o.totalAmount)
       FROM OrderPayment o
       WHERE FUNCTION('YEAR', o.paidAt) = :year
         AND o.status = com.example.football_field_management.model.OrderPayment.Status.COMPLETE
       GROUP BY FUNCTION('MONTH', o.paidAt)
       ORDER BY FUNCTION('MONTH', o.paidAt)
       """)
    List<Object[]> getMonthlyRevenueComplete(@Param("year") int year);
    @Query("""
   SELECT FUNCTION('DAY', o.paidAt), SUM(o.totalAmount)
   FROM OrderPayment o
   WHERE FUNCTION('YEAR', o.paidAt) = :year
     AND FUNCTION('MONTH', o.paidAt) = :month
     AND o.status = 'COMPLETE'
   GROUP BY FUNCTION('DAY', o.paidAt)
   ORDER BY FUNCTION('DAY', o.paidAt)
""")
    List<Object[]> getDailyRevenueInMonth(@Param("year") int year,
                                          @Param("month") int month);
    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM OrderPayment o
        WHERE o.status = 'COMPLETE'
          AND o.paidAt >= :start
          AND o.paidAt < :end
    """)
    BigDecimal sumRevenueBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
    @Query("""
       SELECT o
       FROM OrderPayment o
       WHERE FUNCTION('YEAR', o.paidAt) = :year
         AND FUNCTION('MONTH', o.paidAt) = :month
       """)
    List<OrderPayment> findByYearAndMonth(@Param("year") int year,
                                          @Param("month") int month);
    @Query("SELECT COUNT(DISTINCT o.account.account_id) " +
            "FROM OrderPayment o " +
            "WHERE FUNCTION('YEAR', o.paidAt) = :year " +
            "AND FUNCTION('MONTH', o.paidAt) = :month")
    long countDistinctBuyersByYearAndMonth(@Param("year") int year,
                                           @Param("month") int month);
}



