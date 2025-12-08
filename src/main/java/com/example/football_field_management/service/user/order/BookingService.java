package com.example.football_field_management.service.user.order;

import com.example.football_field_management.dto.BookingDTO;
import com.example.football_field_management.model.*;
import com.example.football_field_management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CourRepository courtRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final AccountRepository accountRepository;

    // =================== TẠO BOOKING ===================
    public Booking createBooking(BookingDTO dto) {

        LocalDate date;
        try {
            date = LocalDate.parse(dto.getBookingDate());
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Invalid date format (YYYY-MM-DD)");
        }

        Cour cour = courtRepository.findById(dto.getCourId())
                .orElseThrow(() -> new RuntimeException("Court does not exist"));

        TimeSlot timeSlot = timeSlotRepository.findById(dto.getTimeSlotId())
                .orElseThrow(() -> new RuntimeException("Time slot does not exist"));

        boolean isBooked = bookingRepository.existsByCourAndTimeSlotAndBookingDate(cour, timeSlot, date);
        if (isBooked) {
            throw new RuntimeException("The court is already booked at this time!");
        }

        Booking booking = new Booking();
        booking.setCour(cour);
        booking.setTimeSlot(timeSlot);
        booking.setBookingDate(date);
        booking.setCustomerName(dto.getCustomerName());
        booking.setPhone(dto.getPhone());
        booking.setEmail(dto.getEmail());
        booking.setNote(dto.getNote());
        booking.setTotalPrice(dto.getPrice());
        booking.setStatus("PENDING");

        if (dto.getAccountId() != null) {
            Account account = accountRepository.findById(dto.getAccountId())
                    .orElseThrow(() -> new RuntimeException("Account does not exist"));

            booking.setAccount(account);

            if (booking.getCustomerName() == null || booking.getCustomerName().isBlank()) {
                booking.setCustomerName(account.getFullName());
            }
            if (booking.getPhone() == null || booking.getPhone().isBlank()) {
                booking.setPhone(account.getPhone());
            }
        }

        return bookingRepository.save(booking);
    }


    public Long getAccountIdByUsername(String username) {
        return accountRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getAccount_id();
    }

    // =================== OWNER: TẤT CẢ BOOKING ===================
    public List<Booking> getBookingsByOwner(String ownerEmail) {
        return bookingRepository.findByCour_Owner_Email(ownerEmail);
    }

    // =================== OWNER: BOOKING HÔM NAY ===================
    public List<Booking> getTodaysBookingsByOwner(String ownerEmail) {
        return bookingRepository.findTodaysBookingsByOwner(ownerEmail, LocalDate.now());
    }

    // =================== OWNER: ĐẾM BOOKING HÔM NAY ===================
    public Long getTodaysBookingCount(String ownerEmail) {
        return bookingRepository.countTodaysBookingsByOwner(ownerEmail, LocalDate.now());
    }

    public Long countTodayBookingsByOwner(String ownerEmail) {
        return bookingRepository.countTodaysBookingsByOwner(ownerEmail, LocalDate.now());
    }
    public Page<Booking> getBookingsByOwnerPaginated(String email, int pageNo, int pageSize) {
        // Tạo đối tượng Pageable (trang hiện tại, số lượng dòng trên 1 trang)
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return bookingRepository.findByCour_Owner_Email_Paginated(email, pageable);
    }
    public Page<Booking> getBookingsByOwner(String ownerEmail, int page) {
        Pageable pageable = PageRequest.of(page, 10); // 10 items/page
        return bookingRepository.findByCour_Owner_Email(ownerEmail, pageable);
    }

    public void approveBooking(Long id, String ownerEmail) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Kiểm tra quyền chủ sân
        if (!booking.getCour().getOwner().getEmail().equals(ownerEmail)) {
            throw new RuntimeException("Owner does not have permission");
        }

        booking.setStatus("APPROVED");
        bookingRepository.save(booking);
    }

    public void rejectBooking(Long id, String ownerEmail) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getCour().getOwner().getEmail().equals(ownerEmail)) {
            throw new RuntimeException("Owner does not have permission");
        }

        booking.setStatus("REJECTED");
        bookingRepository.save(booking);
    }

    public Page<Booking> getBookingsByOwnerAndStatus(String ownerEmail, String status, int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return bookingRepository.findByOwnerAndStatus(ownerEmail, status, pageable);
    }


}
