package com.example.football_field_management.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;                       // mã đơn/phiếu
    private BigDecimal totalAmount;            // số tiền thanh toán
    @Enumerated(EnumType.STRING)
    private Status status;                     // PAID, REFUNDED, CANCELLED
    private String paymentMethod;              // CASH, MOMO, VNPAY,...
    private LocalDateTime paidAt;              // thời điểm thanh toán

    public enum Status { PAID, REFUNDED, CANCELLED }
}