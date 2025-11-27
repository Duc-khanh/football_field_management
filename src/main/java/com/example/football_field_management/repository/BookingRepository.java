package com.example.football_field_management.repository;

import com.example.football_field_management.model.Account;
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

    // Sửa: Thêm @Param cho an toàn, dù Spring mới có thể tự hiểu
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

    // Sửa: Đổi :courtId thành :courId để khớp với @Param("courId")
    @Query("SELECT b.timeSlot.timeSlotId FROM Booking b " +
            "WHERE b.cour.courId = :courId " +   // <--- Đã sửa từ courtId thành courId
            "AND b.bookingDate = :date " +
            "AND b.status != 'CANCELLED'")
    List<Long> findBookedSlots(@Param("courId") Long courtId,
                               @Param("date") LocalDate date);

    // Sửa: Đổi @Param("courtId") thành @Param("courId") để khớp với câu Query :courId
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
            "FROM Booking b " +
            "WHERE b.cour.courId = :courId " +
            "AND b.timeSlot.timeSlotId = :slotId " +
            "AND b.bookingDate = :date " +
            "AND b.status != 'CANCELLED'")
    boolean existsByCourtAndSlotAndDate(@Param("courId") Long courtId, // <--- Đã sửa từ courtId thành courId
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

//    List<Booking> findByAccount(Account account);
//    List<Booking> findByAccount_Account_id(Long accountId);




}