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
    private String code;
    private BigDecimal totalAmount;
    @Enumerated(EnumType.STRING)
    private Status status;
    private String paymentMethod;
    private LocalDateTime paidAt;

    public enum Status { PAID, REFUNDED, CANCELLED,COMPLETE }
}