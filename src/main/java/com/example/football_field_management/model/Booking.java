package com.example.football_field_management.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    // --- Cập nhật theo DTO mới ---
    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "note")
    private String note;
    // -----------------------------

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = true) // Để null nếu cho khách vãng lai đặt
    private Account account;

    @ManyToOne
    @JoinColumn(name = "court_id", nullable = false)
    private Cour cour;

    @OneToOne
    @JoinColumn(name = "payment_id")
    private OrderPayment payment;

    @ManyToOne
    @JoinColumn(name = "slots_id", nullable = false)
    private TimeSlot timeSlot;

    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    @Column(name = "total_price")
    private Double totalPrice;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = "PENDING";
    }
}