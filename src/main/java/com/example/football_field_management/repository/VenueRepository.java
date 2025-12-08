package com.example.football_field_management.repository;

import com.example.football_field_management.model.Venue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {

    // Tìm theo tên sân (ignore case)
    Page<Venue> findByVenueNameContainingIgnoreCase(String keyword, Pageable pageable);

    // Tìm theo status
    Page<Venue> findByStatus(Boolean status, Pageable pageable);

    // Tìm theo tên sân + status
    Page<Venue> findByVenueNameContainingIgnoreCaseAndStatus(String keyword, Boolean status, Pageable pageable);

    // Tìm với từ khóa, districtId và status
    @Query("""
            SELECT v FROM Venue v
            WHERE (:keyword IS NULL OR :keyword = '' OR v.venueName LIKE %:keyword% OR v.address LIKE %:keyword%)
              AND (:districtId IS NULL OR v.district.district_id = :districtId)
              AND (:status IS NULL OR v.status = :status)
            """)
    Page<Venue> searchVenues(@Param("keyword") String keyword,
                             @Param("districtId") Long districtId,
                             @Param("status") Boolean status,
                             Pageable pageable);

    @Query("""
    SELECT v FROM Venue v
    WHERE 
        LOWER(v.venueName) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(v.address) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(v.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(v.district.district_name) LIKE LOWER(CONCAT('%', :keyword, '%'))
""")
    List<Venue> searchVenues(@Param("keyword") String keyword);


    // Đếm số sân của venue
    @Query("""
            SELECT COUNT(c)
            FROM Cour c
            WHERE c.venue.venueId = :venueId
            """)
    int countCourtsByVenue(@Param("venueId") Long venueId);

    // Top 5 venue theo số lượng đơn đặt thành công
    @Query("""
            SELECT v
            FROM OrderPayment op
            JOIN op.booking b
            JOIN b.cour c
            JOIN c.venue v
            WHERE op.status = 'COMPLETE'
            GROUP BY v
            ORDER BY COUNT(op.id) DESC
            """)
    List<Venue> findTop5ByCompletedOrders(Pageable pageable);
}
