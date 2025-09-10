package com.example.football_field_management.repository;

import com.example.football_field_management.model.Venue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue,Long> {
    Page<Venue> findByVenueNameContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Venue> findByStatus(Boolean status, Pageable pageable);

    Page<Venue> findByVenueNameContainingIgnoreCaseAndStatus(String keyword, Boolean status, Pageable pageable);

}
