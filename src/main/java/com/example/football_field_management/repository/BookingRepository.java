package com.example.football_field_management.repository;

import com.example.football_field_management.model.Account;
import com.example.football_field_management.model.Booking;
import com.example.football_field_management.model.Cour;
import com.example.football_field_management.model.TimeSlot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    List<Booking> findBookings(@Param("courId") Long courId, @Param("date") LocalDate date);


    Optional<Booking> findByCour_CourIdAndTimeSlot_TimeSlotIdAndBookingDate(
            Long courId,
            Long timeSlotId,
            LocalDate bookingDate
    );

    @Query("SELECT b.timeSlot.timeSlotId FROM Booking b " +
            "WHERE b.cour.courId = :courId " +
            "AND b.bookingDate = :date " +
            "AND b.status != 'CANCELLED'")
    List<Long> findBookedSlots(@Param("courId") Long courtId,
                               @Param("date") LocalDate date);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
            "FROM Booking b " +
            "WHERE b.cour.courId = :courId " +
            "AND b.timeSlot.timeSlotId = :slotId " +
            "AND b.bookingDate = :date " +
            "AND b.status != 'CANCELLED'")
    boolean existsByCourtAndSlotAndDate(@Param("courId") Long courtId,
                                        @Param("slotId") Long slotId,
                                        @Param("date") LocalDate date);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.cour.courId = :courId " +
            "AND b.bookingDate = :date " +
            "AND (b.status IS NULL OR b.status != 'CANCELLED')")
    List<Booking> findBookingsByDateAndCourt(@Param("courId") Long courId,
                                             @Param("date") LocalDate date);

    @Query("SELECT b FROM Booking b WHERE b.account.account_id = :accountId")
    List<Booking> findByAccountId(@Param("accountId") Long accountId);

    @Query("""
            SELECT b FROM Booking b
            WHERE 
               (:status IS NULL OR b.status = :status)
            AND (
                   LOWER(b.account.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(b.account.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            """)
    Page<Booking> searchOrder(
            @Param("keyword") String keyword,
            @Param("status") String status,
            Pageable pageable
    );

    // ================= OWNER QUERY CHUẨN =================
    List<Booking> findByCour_Owner(Account owner);

    List<Booking> findByCour_Owner_Email(String ownerEmail);

    boolean existsByCourAndTimeSlotAndBookingDate(Cour cour, TimeSlot slot, LocalDate date);

    List<Booking> findByAccountEmailAndBookingDate(String email, LocalDate date);

    @Query("SELECT b FROM Booking b WHERE b.cour.owner.email = :ownerEmail AND b.bookingDate = :today " +
            "AND (b.status IS NULL OR b.status != 'CANCELLED')")
    List<Booking> findTodaysBookingsByOwner(@Param("ownerEmail") String ownerEmail,
                                            @Param("today") LocalDate today);


    @Query("""
            SELECT COUNT(b)
            FROM Booking b
            WHERE b.cour.owner.email = :ownerEmail
              AND b.bookingDate = :today
            """)
    Long countTodaysBookingsByOwner(@Param("ownerEmail") String ownerEmail,
                                    @Param("today") LocalDate today);

}
