package com.example.football_field_management.service.user.order;

import com.example.football_field_management.model.Booking;
import com.example.football_field_management.model.Cour; // Hoặc Court tùy tên class bạn
import com.example.football_field_management.model.TimeSlot;
import com.example.football_field_management.repository.BookingRepository;
import com.example.football_field_management.repository.CourRepository; // Hoặc CourtRepository
import com.example.football_field_management.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        List<Map<String, Object>> result = new ArrayList<>();

        // 1. Lấy thông tin Sân (Nếu không tìm thấy sân, vẫn trả về list rỗng để tránh lỗi null)
        Optional<Cour> courOpt = courRepo.findById(courId);
        if (courOpt.isEmpty()) {
            System.out.println("Không tìm thấy sân với ID: " + courId);
            return result;
        }

        // 2. Lấy tất cả các khung giờ từ DB
        List<TimeSlot> allSlots = timeSlotRepo.findAll();
        if (allSlots.isEmpty()) {
            System.out.println("Bảng time_slot chưa có dữ liệu!");
            return result;
        }

        // 3. Duyệt từng ngày từ Start -> End
        LocalDate current = start;
        while (!current.isAfter(end)) {
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", current.toString());
            dayData.put("dayOfWeek", getDayName(current));

            // Tìm các booking đã đặt trong ngày này & sân này (trừ đơn hủy)
            // Lưu ý: Cần đảm bảo BookingRepository có hàm findBookingsByDateAndCourt
            List<Booking> bookings = bookingRepo.findBookingsByDateAndCourt(courId, current);

            // Tạo set các ID đã đặt để check nhanh
            Set<Long> bookedSlotIds = bookings.stream()
                    .map(b -> b.getTimeSlot().getTimeSlotId()) // Sửa lại getter cho đúng Entity của bạn
                    .collect(Collectors.toSet());

            // Map danh sách slot sang DTO
            List<Map<String, Object>> slotsDTO = new ArrayList<>();
            for (TimeSlot slot : allSlots) {
                Map<String, Object> s = new HashMap<>();
                s.put("id", slot.getTimeSlotId()); // ID khung giờ

                // Format giờ (cắt giây)
                String startTime = slot.getStartTime().toString();
                String endTime = slot.getEndTime().toString();
                if(startTime.length() > 5) startTime = startTime.substring(0, 5);
                if(endTime.length() > 5) endTime = endTime.substring(0, 5);

                s.put("time", startTime + " - " + endTime);
                s.put("price", 200000); // Giá (Có thể lấy từ courOpt.get().getPrice())
                s.put("isBooked", bookedSlotIds.contains(slot.getTimeSlotId())); // Trạng thái

                slotsDTO.add(s);
            }

            dayData.put("slots", slotsDTO);
            result.add(dayData);

            current = current.plusDays(1);
        }

        return result;
    }

    private String getDayName(LocalDate date) {
        int day = date.getDayOfWeek().getValue(); // 1 = Monday, 7 = Sunday
        switch (day) {
            case 1: return "Thứ 2";
            case 2: return "Thứ 3";
            case 3: return "Thứ 4";
            case 4: return "Thứ 5";
            case 5: return "Thứ 6";
            case 6: return "Thứ 7";
            case 7: return "Chủ Nhật";
            default: return "";
        }
    }
}