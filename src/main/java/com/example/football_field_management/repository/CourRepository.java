package com.example.football_field_management.repository;


import com.example.football_field_management.model.Cour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourRepository extends JpaRepository<Cour, Long> {
    List<Cour> findByStatus(Boolean status);

    List<Cour> findByVenue_VenueId(Long venueId);

    List<Cour> findBySurfaceType(String surfaceType);

    Page<Cour> findByCourNameContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Cour> findByStatus(Boolean status, Pageable pageable);

    Page<Cour> findByCourNameContainingIgnoreCaseAndStatus(String keyword, Boolean status, Pageable pageable);

}


