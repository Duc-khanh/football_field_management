package com.example.football_field_management.repository;

import com.example.football_field_management.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue,Long> {
    List<Venue> findByVenueNameContainingIgnoreCase(String venueName);

}
