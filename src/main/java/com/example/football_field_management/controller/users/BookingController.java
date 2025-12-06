package com.example.football_field_management.controller.users;

import com.example.football_field_management.dto.BookingDTO;
import com.example.football_field_management.dto.TimeSlotStatusDTO;
import com.example.football_field_management.model.Account;
import com.example.football_field_management.model.Booking;
import com.example.football_field_management.model.TimeSlot;
import com.example.football_field_management.repository.AccountRepository;
import com.example.football_field_management.repository.BookingRepository;
import com.example.football_field_management.repository.TimeSlotRepository;
import com.example.football_field_management.service.user.order.BookingService;
import com.example.football_field_management.service.user.order.TimeslotService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final TimeSlotRepository timeSlotRepository;
    private final BookingRepository bookingRepository;
    private final TimeslotService timeslotService;
    private final AccountRepository accountRepository;


    // Lấy slot theo tuần
    @GetMapping("/timeslots")
    public List<TimeSlotStatusDTO> getTimeSlots(
            @RequestParam Long courId,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        List<TimeSlot> slots = timeSlotRepository.findByCour_CourIdAndDateBetween(courId, start, end);

        return slots.stream()
                .map(slot -> TimeSlotStatusDTO.fromEntity(slot, false))
                .toList();
    }

    // Lấy lịch trống theo ngày
    @GetMapping("/availability")
    public ResponseEntity<?> getAvailability(
            @RequestParam Long courtId,
            @RequestParam String date
    ) {
        try {
            LocalDate targetDate = LocalDate.parse(date);
            List<TimeSlot> allSlots = timeSlotRepository.findAll();
            if (allSlots.isEmpty()) return ResponseEntity.ok(List.of());

            List<Long> bookedSlotIds = bookingRepository.findBookedSlots(courtId, targetDate);

            List<Map<String, Object>> response = allSlots.stream().map(slot -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", slot.getTimeSlotId());
                String start = slot.getStartTime().toString();
                String end = slot.getEndTime().toString();
                if (start.length() > 5) start = start.substring(0, 5);
                if (end.length() > 5) end = end.substring(0, 5);
                map.put("time", start + " - " + end);
                map.put("price", 200000);
                map.put("isBooked", bookedSlotIds.contains(slot.getTimeSlotId()));
                return map;
            }).toList();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi format ngày hoặc server: " + e.getMessage());
        }
    }

    // Tạo booking
    @PostMapping
    public ResponseEntity<?> createBooking(
            @RequestBody BookingDTO dto,
            Authentication authentication
    ) {
        try {
            if (authentication == null) {
                return ResponseEntity.status(401)
                        .body(Map.of("message", "Bạn cần đăng nhập để đặt sân!"));
            }

            String username = authentication.getName();
            dto.setCustomerName(username);

            // Gán accountId tự động
            Long accountId = bookingService.getAccountIdByUsername(username);
            dto.setAccountId(accountId);

            Booking newBooking = bookingService.createBooking(dto);

            return ResponseEntity.ok(newBooking);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    // Lấy danh sách sân đã đặt của tôi
    @GetMapping("/my-bookings")
    public ResponseEntity<?> getMyBookings(Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Bạn chưa đăng nhập!"));
        }

        try {
            String username = authentication.getName();

            // Lấy accountId từ username
            Long accountId = bookingService.getAccountIdByUsername(username);

            // Lấy danh sách booking theo accountId
            List<Booking> bookings = bookingRepository.findByAccountId(accountId);

            // Trả dữ liệu gọn nhẹ cho frontend
            List<Map<String, Object>> response = bookings.stream().map(b -> {
                Map<String, Object> map = new HashMap<>();
                map.put("bookingId", b.getBookingId());
                map.put("courtName", b.getCour().getCourName());
                map.put("date", b.getBookingDate());
                map.put("time", b.getTimeSlot().getStartTime() + " - " + b.getTimeSlot().getEndTime());
                map.put("price", b.getTotalPrice());
                map.put("status", b.getStatus());
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Không lấy được dữ liệu đặt sân!"));
        }
    }
    @GetMapping("/owner/bookings")
    public String viewBookings(Model model,
                               @AuthenticationPrincipal UserDetails userDetails,
                               @RequestParam(defaultValue = "0") int page) { // Nhận tham số page từ URL (mặc định là 0)

        // 1. Kiểm tra User
        if (userDetails == null) return "redirect:/login";

        Account owner = accountRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        // 2. Cấu hình số lượng dòng trên 1 trang (Ví dụ: 5 dòng)
        int pageSize = 5;

        // 3. Lấy dữ liệu phân trang từ Service
        Page<Booking> bookingPage = bookingService.getBookingsByOwnerPaginated(owner.getEmail(), page, pageSize);

        model.addAttribute("bookings", bookingPage.getContent()); // Danh sách booking
        model.addAttribute("currentPage", page);                  // Trang hiện tại
        model.addAttribute("totalPages", bookingPage.getTotalPages()); // Tổng số trang
        model.addAttribute("totalItems", bookingPage.getTotalElements()); // Tổng số bản ghi


        return "owner/bookings";
    }

}
