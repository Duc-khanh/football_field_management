package com.example.football_field_management.dto;

import lombok.Data;

@Data
public class BookingRequest {
    private Long accountId;
    private Long courtId;
    private Long slotsId;
    private String date;
    private int hours;
}
