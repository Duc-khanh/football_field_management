package com.example.football_field_management.service.booking;

import com.example.football_field_management.dto.BookingRequest;
import com.example.football_field_management.model.Account;
import com.example.football_field_management.model.Booking;
import com.example.football_field_management.model.Cour;
import com.example.football_field_management.model.TimeSlot;
import com.example.football_field_management.repository.AccountRepository;
import com.example.football_field_management.repository.BookingRepository;
import com.example.football_field_management.repository.CourRepository;
import com.example.football_field_management.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CourRepository courRepository;
    private final AccountRepository accountRepository;
    private final TimeSlotRepository timeSlotRepository;

    @Transactional
    public Booking createBooking(BookingRequest request) {
        Cour cour = courRepository.findById(request.getCourtId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sân"));

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));

        TimeSlot slot = timeSlotRepository.findById(request.getSlotsId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khung giờ"));

        LocalDate date = LocalDate.parse(request.getDate());

        double totalPrice = request.getHours() * cour.getPricePerHour();

        Booking booking = Booking.builder()
                .account(account)
                .cour(cour)
                .slot(slot)
                .bookingDate(date)
                .totalPrice(totalPrice)
                .status("pending")
                .build();

        return bookingRepository.save(booking);
    }

}

