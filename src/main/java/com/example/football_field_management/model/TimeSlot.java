package com.example.football_field_management.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "time_slot")
@Getter
@Setter
public class TimeSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long timeSlotId;
    @Column(name = "start_time")
    private java.time.LocalTime startTime;

    @Column(name = "end_time")
    private java.time.LocalTime endTime;
    @Column(name = "date")
    private LocalDate date;
    @ManyToOne
    @JoinColumn(name = "cour_id")
    private Cour cour;

}

