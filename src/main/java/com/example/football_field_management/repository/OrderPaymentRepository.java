package com.example.football_field_management.repository;

import com.example.football_field_management.dto.CustomerSpentDTO;
import com.example.football_field_management.dto.MonthlyRevenueDTO;
import com.example.football_field_management.model.OrderPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM OrderPayment o
        WHERE o.status = 'COMPLETE'
          AND FUNCTION('YEAR', o.paidAt) = :year
    """)
    BigDecimal sumRevenueByYear(@Param("year") int year);

    /** Lấy tất cả order trong 1 năm */
    @Query("""
        SELECT o
        FROM OrderPayment o
        WHERE FUNCTION('YEAR', o.paidAt) = :year
    """)
    List<OrderPayment> findByYear(@Param("year") int year);

    /** Đếm số người mua duy nhất trong năm */
    @Query("""
        SELECT COUNT(DISTINCT o.account.account_id)
        FROM OrderPayment o
        WHERE FUNCTION('YEAR', o.paidAt) = :year
    """)
    long countDistinctBuyersByYear(@Param("year") int year);
    @Query("""
    SELECT COALESCE(SUM(o.totalAmount), 0)
    FROM OrderPayment o
    WHERE DATE(o.paidAt) = :day
      AND o.status = 'PAID'
""")
    BigDecimal getRevenueByDay(@Param("day") LocalDate day);
    @Query("""
    SELECT new com.example.football_field_management.dto.CustomerSpentDTO(
         a.account_id,
         a.fullName,
         CAST(SUM(o.totalAmount) AS bigdecimal),
         a.avt_path,
         a.email,
         a.address
     )
    FROM OrderPayment o
    JOIN o.account a
    WHERE o.status = com.example.football_field_management.model.OrderPayment.Status.COMPLETE
      AND (:year IS NULL OR FUNCTION('YEAR', o.paidAt) = :year)
      AND (:month IS NULL OR FUNCTION('MONTH', o.paidAt) = :month)
    GROUP BY a.account_id, a.fullName, a.avt_path, a.email, a.address
    ORDER BY SUM(o.totalAmount) DESC
""")
    List<CustomerSpentDTO> findCustomerSpentByYearMonth(@Param("year") Integer year,
                                                        @Param("month") Integer month,
                                                        Pageable pageable);


    @Query("SELECT o FROM OrderPayment o " +
            "WHERE (:year IS NULL OR YEAR(o.paidAt) = :year) " +
            "AND (:month IS NULL OR MONTH(o.paidAt) = :month)")
    Page<OrderPayment> findOrdersByYearAndMonth(
            @Param("year") Integer year,
            @Param("month") Integer month,
            Pageable pageable
    );
    // Đếm đơn hôm nay
    @Query("SELECT COUNT(o) FROM OrderPayment o WHERE DATE(o.paidAt) = CURRENT_DATE AND o.status = 'COMPLETE'")
    long countTodayOrders();

    // Tổng doanh thu tháng hiện tại
    @Query("SELECT COALESCE(SUM(o.totalAmount),0) FROM OrderPayment o " +
            "WHERE MONTH(o.paidAt) = MONTH(CURRENT_DATE) " +
            "AND YEAR(o.paidAt) = YEAR(CURRENT_DATE) " +
            "AND o.status = 'COMPLETE'")
    BigDecimal getRevenueThisMonth();
    Optional<OrderPayment> findByCode(String code);

}

