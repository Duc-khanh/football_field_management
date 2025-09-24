package com.example.football_field_management.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CourDTO {
    private Long courId;
    private String courName;
    private double pricePerHour;
    private String fieldSize;
    private boolean lightsAvailable;
    private String surfaceType;
    private Boolean status;
    private String image;



    public CourDTO(Long courId, String courName, double pricePerHour, String fieldSize, boolean lightsAvailable, String surfaceType, Boolean status, String image) {
        this.courId = courId;
        this.courName = courName;
        this.pricePerHour = pricePerHour;
        this.fieldSize = fieldSize;
        this.lightsAvailable = lightsAvailable;
        this.surfaceType = surfaceType;
        this.status = status;
        this.image = image;
    }
}

