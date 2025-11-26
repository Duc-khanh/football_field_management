package com.example.football_field_management.controller.users;

import com.example.football_field_management.dto.BookingDTO;
import com.example.football_field_management.dto.TimeSlotStatusDTO;
import com.example.football_field_management.model.Booking;
import com.example.football_field_management.model.TimeSlot;
import com.example.football_field_management.service.user.order.BookingService;
import com.example.football_field_management.service.user.order.ITimeslotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final ITimeslotService timeslotService;

    // Lấy khung giờ theo tuần
    @GetMapping("/timeslots")
    public List<Map<String, Object>> getWeeklySlots(
            @RequestParam Long courId,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return timeslotService.getWeeklySlots(courId, start, end);
    }

    // Tạo booking mới
    @PostMapping("/create")
    public Booking createBooking(@RequestBody BookingDTO dto) {
        return bookingService.createBooking(dto);
    }
}
