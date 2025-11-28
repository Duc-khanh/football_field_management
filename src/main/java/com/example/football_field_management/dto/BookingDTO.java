package com.example.football_field_management.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingDTO {
    private Long courId;
    private Long timeSlotId;
    private String bookingDate;
    private String customerName;
    private String email;
    private String phone;
    private Double duration;
    private Double price;
    private String note;
    private Long accountId;
}
