package com.example.football_field_management.repository;

import com.example.football_field_management.model.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    List<TimeSlot> findByCour_CourId(Long courId);
    List<TimeSlot> findByCour_CourIdAndDateBetween(Long courId, LocalDate start, LocalDate end);

}
