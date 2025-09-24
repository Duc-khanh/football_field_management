package com.example.football_field_management.controller.booking;


import com.example.football_field_management.model.TimeSlot;
import com.example.football_field_management.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timeslots")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class TimeSlotController {

    private final TimeSlotRepository timeSlotRepository;

    @GetMapping
    public List<TimeSlot> getAllTimeSlots() {
        return timeSlotRepository.findAll();
    }
}
