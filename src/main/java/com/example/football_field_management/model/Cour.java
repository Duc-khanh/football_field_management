package com.example.football_field_management.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "cour")
public class Cour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cour_id;

    private String cour_name;

    private Double price_per_hour;

    private String field_size;

    private Boolean lights_available;

    private String surface_type;

    private Boolean status;

    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;
}

