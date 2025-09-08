package com.example.football_field_management.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class CourDTO {
    private Long courId;
    private String courName;
    private double pricePerHour;
    private String fieldSize;
    private boolean lightsAvailable;
    private String surfaceType;
    private String status;
}

