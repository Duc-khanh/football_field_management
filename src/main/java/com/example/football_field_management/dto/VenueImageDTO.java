package com.example.football_field_management.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VenueImageDTO {
    private Long photoId;
    private String photoPath;
    private boolean isPrimary;
}
