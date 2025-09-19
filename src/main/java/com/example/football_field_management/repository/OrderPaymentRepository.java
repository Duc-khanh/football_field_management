package com.example.football_field_management.repository;

import com.example.football_field_management.dto.MonthlyRevenueDTO;
import com.example.football_field_management.model.OrderPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


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
}



