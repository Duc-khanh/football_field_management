package com.example.football_field_management.service.user.order;

import com.example.football_field_management.dto.BookingDTO;
import com.example.football_field_management.model.Booking;

public interface IBookingService {
    Booking createBooking(BookingDTO dto);
}
