package com.example.football_field_management.service.user.order;

import com.example.football_field_management.dto.BookingDTO;
import com.example.football_field_management.model.*;
import com.example.football_field_management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CourRepository courtRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final AccountRepository accountRepository;

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

        boolean isBooked = bookingRepository.existsByCourtAndSlotAndDate(dto.getCourId(), dto.getTimeSlotId(), date);
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

        if (dto.getAccountId() != null) {
            Account account = accountRepository.findById(dto.getAccountId())
                    .orElseThrow(() -> new RuntimeException("Account does not exist"));
            booking.setAccount(account);
            if (booking.getCustomerName() == null || booking.getCustomerName().isEmpty()) {
                booking.setCustomerName(account.getFullName());
            }
            if (booking.getPhone() == null || booking.getPhone().isEmpty()) {
                booking.setPhone(account.getPhone());
            }
        }

        booking.setTotalPrice(dto.getPrice());
        booking.setStatus("PENDING");

        return bookingRepository.save(booking);
    }

    public Long getAccountIdByUsername(String username) {
        Account account = accountRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return account.getAccount_id();
    }
}