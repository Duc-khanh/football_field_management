package com.example.football_field_management.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Cour")
public class Cour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cour_id")
    private Long courId;

    @Column(name = "cour_name")
    private String courName;

    @Column(name = "price_per_hour")
    private Double pricePerHour;

    @Column(name = "field_size")
    private String fieldSize;

    @Column(name = "lights_available")
    private Boolean lightsAvailable;

    @Column(name = "surface_type")
    private String surfaceType;

    @Column(name = "status")
    private Boolean status;

    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;
}