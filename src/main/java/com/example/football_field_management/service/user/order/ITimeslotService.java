package com.example.football_field_management.service.user.order;

import com.example.football_field_management.model.Cour;
import com.example.football_field_management.model.TimeSlot;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ITimeslotService {
    List<Map<String, Object>> getWeeklySlots(Long courId, LocalDate start, LocalDate end);
}
