package com.example.football_field_management.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingRequest {
    private Long courId;
    private Long accountId; // ID của người dùng đang đăng nhập
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}