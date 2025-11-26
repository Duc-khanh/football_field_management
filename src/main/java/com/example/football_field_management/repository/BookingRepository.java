package com.example.football_field_management.repository;

import com.example.football_field_management.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
    SELECT b 
    FROM Booking b 
    WHERE b.cour.courId = :courId 
      AND b.bookingDate = :date
""")
    List<Booking> findBookings(Long courId, LocalDate date);


    Optional<Booking> findByCour_CourIdAndTimeSlot_TimeSlotIdAndBookingDate(
            Long courId,
            Long timeSlotId,
            LocalDate bookingDate
    );
}

