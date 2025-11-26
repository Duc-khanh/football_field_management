package com.example.football_field_management.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor; // ❗️ THÊM IMPORT NÀY
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CourDTO {
    private Long courId;
    private String courName;
    private double pricePerHour;
    private String fieldSize;
    private boolean lightsAvailable;
    private String surfaceType;
    private String status;

    @Column(name = "courId")
    private Long courtId;

    public CourDTO(Long courId, String courName, String fieldSize, Double pricePerHour) {
        this.courId = courId;
        this.courName = courName;
        this.fieldSize = fieldSize;
        this.pricePerHour = pricePerHour;
    }



    public CourDTO(Long courId, String courName, double pricePerHour,
                   String fieldSize, boolean lightsAvailable,
                   String surfaceType, Boolean statusEntity) {


        this.courId = courId;
        this.courName = courName;
        this.pricePerHour = pricePerHour;
        this.fieldSize = fieldSize;
        this.lightsAvailable = lightsAvailable;
        this.surfaceType = surfaceType;

        if (statusEntity == null) {
            this.status = "Không xác định";
        } else {
            this.status = statusEntity ? "Hoạt động" : "Bảo trì";
        }
    }
}