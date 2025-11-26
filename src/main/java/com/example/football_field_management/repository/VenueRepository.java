package com.example.football_field_management.repository;

import com.example.football_field_management.model.Venue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {
    Page<Venue> findByVenueNameContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Venue> findByStatus(Boolean status, Pageable pageable);

    Page<Venue> findByVenueNameContainingIgnoreCaseAndStatus(String keyword, Boolean status, Pageable pageable);

    @Query("SELECT v FROM Venue v JOIN OrderPayment op ON op.id = v.owner.account_id " +
            "WHERE op.status = 'COMPLETE' " +
            "GROUP BY v.venueId " +
            "ORDER BY COUNT(op.id) DESC")
    List<Venue> findTop5ByCompletedOrders(Pageable pageable);

    @Query("""
SELECT v FROM Venue v
WHERE (:keyword IS NULL OR v.venueName LIKE %:keyword%)
  AND (:status IS NULL OR v.status = :status)
""")
    Page<Venue> findAllWithRelations(@Param("keyword") String keyword,
                                     @Param("status") Boolean status,
                                     Pageable pageable);
    @Query("""
    SELECT COUNT(c)
    FROM Cour c
    WHERE c.venue.venueId = :venueId
""")
    int countCourtsByVenue(@Param("venueId") Long venueId);


}
