package com.example.football_field_management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TimeSlotStatusDTO {
    private Long timeSlotId;
    private String start;
    private String end;
    private boolean booked;
}

