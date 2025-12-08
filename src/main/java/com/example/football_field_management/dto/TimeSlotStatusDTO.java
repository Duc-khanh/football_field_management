package com.example.football_field_management.dto;

import com.example.football_field_management.model.TimeSlot;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TimeSlotStatusDTO {
    private Long time_slot_id;
    private String start_time;
    private String end_time;
    private String status;
    private int price;
    private String date; // <--- THÊM TRƯỜNG NÀY

    public static TimeSlotStatusDTO fromEntity(TimeSlot ts, boolean isBooked) {
        TimeSlotStatusDTO dto = new TimeSlotStatusDTO();
        dto.setTime_slot_id(ts.getTimeSlotId());

        // Chuyển LocalTime sang String (ví dụ: 18:00)
        dto.setStart_time(ts.getStartTime().toString());
        dto.setEnd_time(ts.getEndTime().toString());

        dto.setStatus(isBooked ? "booked" : "available");
        dto.setPrice(800); // Lấy giá từ DB nếu có

        // Chuyển LocalDate sang String (YYYY-MM-DD)
        if (ts.getDate() != null) {
            dto.setDate(ts.getDate().toString());
        }

        return dto;
    }
}