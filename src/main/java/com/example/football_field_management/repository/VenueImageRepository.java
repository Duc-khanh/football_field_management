package com.example.football_field_management.repository;

import com.example.football_field_management.model.VenueImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueImageRepository extends JpaRepository<VenueImage,Long> {
    List<VenueImage> findByVenue_VenueId(Long venueId);
}
