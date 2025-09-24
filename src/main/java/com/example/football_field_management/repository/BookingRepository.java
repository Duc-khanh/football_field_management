package com.example.football_field_management.repository;

import com.example.football_field_management.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {

}