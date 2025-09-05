package com.example.football_field_management.repository;

import com.example.football_field_management.dto.RevenuePoint;
import com.example.football_field_management.model.OrderPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// com.example.football_field_management.repository.OrderPaymentRepository
public interface OrderPaymentRepository extends JpaRepository<OrderPayment, Long> {

    // Theo NGÀY
    @Query("""
                select DATE(p.paidAt) as label,
                       COALESCE(sum(p.totalAmount),0) as amount,
                       count(p.id) as orders
                from OrderPayment p
                where p.status = 'PAID' and p.paidAt between :from and :to
                group by DATE(p.paidAt)
                order by DATE(p.paidAt)
            """)
    List<RevenuePoint> revenueByDay(@Param("from") LocalDateTime from,
                                    @Param("to") LocalDateTime to);

    // Theo THÁNG (MySQL format YYYY-MM)
    @Query(value = """
                select DATE_FORMAT(paid_at, '%Y-%m') as label,
                       COALESCE(sum(total_amount),0)  as amount,
                       count(id) as orders
                from order_payment
                where status = 'PAID' and paid_at between :from and :to
                group by DATE_FORMAT(paid_at, '%Y-%m')
                order by DATE_FORMAT(paid_at, '%Y-%m')
            """, nativeQuery = true)
    List<RevenuePoint> revenueByMonth(@Param("from") LocalDateTime from,
                                      @Param("to") LocalDateTime to);

    // Theo NĂM
    @Query(value = """
                select DATE_FORMAT(paid_at, '%Y') as label,
                       COALESCE(sum(total_amount),0)  as amount,
                       count(id) as orders
                from order_payment
                where status = 'PAID' and paid_at between :from and :to
                group by DATE_FORMAT(paid_at, '%Y')
                order by DATE_FORMAT(paid_at, '%Y')
            """, nativeQuery = true)
    List<RevenuePoint> revenueByYear(@Param("from") LocalDateTime from,
                                     @Param("to") LocalDateTime to);

    // KPI tổng
    @Query("""
                select COALESCE(sum(p.totalAmount),0) from OrderPayment p
                where p.status='PAID' and p.paidAt between :from and :to
            """)
    BigDecimal totalRevenue(@Param("from") LocalDateTime from,
                            @Param("to") LocalDateTime to);

    @Query("""
                select count(p.id) from OrderPayment p
                where p.status='PAID' and p.paidAt between :from and :to
            """)
    long totalOrders(@Param("from") LocalDateTime from,
                     @Param("to") LocalDateTime to);
}

