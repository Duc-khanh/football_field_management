package com.example.football_field_management.service.user.order;

import com.example.football_field_management.model.Booking;
import com.example.football_field_management.model.Cour;
import com.example.football_field_management.model.TimeSlot;
import com.example.football_field_management.repository.BookingRepository;
import com.example.football_field_management.repository.CourRepository;
import com.example.football_field_management.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeslotService implements ITimeslotService {

    private final TimeSlotRepository timeSlotRepo;
    private final BookingRepository bookingRepo;
    private final CourRepository courRepo;

    @Override
    public List<Map<String, Object>> getWeeklySlots(Long courId, LocalDate start, LocalDate end) {
        // Lấy thông tin sân để lấy giá
        Optional<Cour> courOpt = courRepo.findById(courId);
        if (courOpt.isEmpty()) {
            return Collections.emptyList();
        }
        Cour cour = courOpt.get();
        Double price = cour.getPricePerHour();

        // Lấy tất cả khung giờ
        List<TimeSlot> slots = timeSlotRepo.findAll();

        List<Map<String, Object>> result = new ArrayList<>();

        LocalDate date = start;
        while (!date.isAfter(end)) {
            // Lấy booking đã có cho sân & ngày
            List<Booking> bookings = bookingRepo.findBookings(courId, date);
            Map<Long, Boolean> bookedMap = bookings.stream()
                    .collect(Collectors.toMap(
                            b -> b.getTimeSlot().getId(),  // <-- sửa ở đây
                            b -> true
                    ));

            List<Map<String, Object>> slotDTOs = slots.stream().map(s -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", s.getId());
                map.put("time", s.getStartTime().substring(0,5) + " - " + s.getEndTime().substring(0,5));
                map.put("status", bookedMap.getOrDefault(s.getId(), false) ? "booked" : "available");
                map.put("price", price);
                return map;
            }).collect(Collectors.toList());


            result.add(Map.of(
                    "date", date.toString(),
                    "dayOfWeek", getDayOfWeek(date.getDayOfWeek()),
                    "slots", slotDTOs
            ));

            date = date.plusDays(1);
        }

        return result;
    }

    private String getDayOfWeek(DayOfWeek day) {
        return switch (day) {
            case MONDAY -> "T2";
            case TUESDAY -> "T3";
            case WEDNESDAY -> "T4";
            case THURSDAY -> "T5";
            case FRIDAY -> "T6";
            case SATURDAY -> "T7";
            default -> "CN";
        };
    }
}
