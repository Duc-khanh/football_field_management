package com.example.football_field_management.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cour_id")
    private Cour cour;

    @ManyToOne
    @JoinColumn(name = "time_slot_id")
    private TimeSlot timeSlot;

    private LocalDate bookingDate;

    private String customerName;
    private String email;
    private String phone;

    private Double duration;
    private Double price;
    private String note;
    @OneToOne
    @JoinColumn(name = "payment_id")
    private OrderPayment payment;


}

//    private Long bookingId;
//
//    @ManyToOne
//    @JoinColumn(name = "cour_id", nullable = false)
//    private Cour cour;
//
//    @ManyToOne
//    @JoinColumn(name = "account_id", nullable = false)
//    private Account account; // Giả sử bạn có model Account cho người dùng
//
//    @Column(name = "start_time", nullable = false)
//    private LocalDateTime startTime;
//
//    @Column(name = "end_time", nullable = false)
//    private LocalDateTime endTime;
//
//    @Column(name = "total_price")
//    private Double totalPrice;
//
//    @Column(name = "booking_status")
//    private String status; // Ví dụ: "PENDING", "CONFIRMED", "CANCELLED"
//
//    @Column(name = "created_at", updatable = false)
//    @Builder.Default
//    private LocalDateTime createdAt = LocalDateTime.now();
//}
