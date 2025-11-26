package com.example.football_field_management.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalTime;

@Entity
@Table(name = "time_slot")
@Getter
@Setter
public class TimeSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long timeSlotId;
    private String startTime;
    private String endTime;
    @ManyToOne
    @JoinColumn(name = "cour_id")
    private Cour cour;
}

