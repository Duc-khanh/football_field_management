package com.example.football_field_management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "court_id", nullable = false)
    private Cour cour;

    @ManyToOne
    @JoinColumn(name = "slots_id", nullable = false)
    private TimeSlot slot;

    // Ngày đặt sân
    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    // Giá tổng
    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    // Trạng thái: pending | confirmed | cancelled
    @Column(nullable = false)
    private String status;

    // Ngày tạo, DB tự sinh
    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;
}
