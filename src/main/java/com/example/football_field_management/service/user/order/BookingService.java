package com.example.football_field_management.service.user.order;

import com.example.football_field_management.dto.BookingDTO;
import com.example.football_field_management.model.Booking;
import com.example.football_field_management.model.Cour;
import com.example.football_field_management.model.TimeSlot;
import com.example.football_field_management.repository.BookingRepository;
import com.example.football_field_management.repository.CourRepository;
import com.example.football_field_management.repository.TimeSlotRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService implements IBookingService {

    private final TimeSlotRepository timeSlotRepository;
    private final BookingRepository bookingRepository;
    private final CourRepository courRepository;

    @Override
    public Booking createBooking(BookingDTO dto) {

        Cour cour = courRepository.findById(dto.getCourId())
                .orElseThrow(() -> new RuntimeException("Cour not found"));

        TimeSlot slot = timeSlotRepository.findById(dto.getTimeSlotId())
                .orElseThrow(() -> new RuntimeException("TimeSlot not found"));

        LocalDate bookingDate = LocalDate.parse(dto.getBookingDate());

        Optional<Booking> existedBooking =
                bookingRepository.findByCour_CourIdAndTimeSlot_TimeSlotIdAndBookingDate(
                        dto.getCourId(),
                        dto.getTimeSlotId(),
                        bookingDate
                );

        if (existedBooking.isPresent()) {
            throw new RuntimeException("This slot is already booked");
        }

        Booking booking = new Booking();
        booking.setCour(cour);
        booking.setTimeSlot(slot);
        booking.setBookingDate(bookingDate);
        booking.setCustomerName(dto.getCustomerName());
        booking.setEmail(dto.getEmail());
        booking.setPhone(dto.getPhone());
        booking.setDuration(dto.getDuration());
        booking.setPrice(dto.getPrice());
        booking.setNote(dto.getNote());

        return bookingRepository.save(booking);
    }
}
