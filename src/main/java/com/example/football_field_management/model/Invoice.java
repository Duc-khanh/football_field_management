package com.example.football_field_management.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Long invoiceId;

    // ------- Booking (FK) -------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    // ------- Payment Method (FK) -------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", nullable = false)
    private OrderPayment orderPayment;

    // ------- Payment Date -------
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    // ------- Status -------
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    public enum Status {
        PAID,        // Đã thanh toán
        UNPAID,      // Chưa thanh toán
        CANCELLED,   // Hủy
        REFUNDED     // Hoàn tiền
    }
}
