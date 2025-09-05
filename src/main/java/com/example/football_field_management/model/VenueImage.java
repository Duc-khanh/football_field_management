package com.example.football_field_management.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "venue_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VenueImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long photoId;

    private String photoPath;
    private boolean isPrimary;

    @ManyToOne
    @JoinColumn(name = "venue_id")
    private Venue venue;
}

