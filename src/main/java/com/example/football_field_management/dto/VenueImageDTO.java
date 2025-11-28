package com.example.football_field_management.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VenueImageDTO {
    private Long photoId;
    private String photoPath;
    private boolean isPrimary;
}
