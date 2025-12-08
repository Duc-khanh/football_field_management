package com.example.football_field_management.controller.owner;

import com.example.football_field_management.model.Booking;
import com.example.football_field_management.model.Account;
import com.example.football_field_management.service.user.order.BookingService;
import com.example.football_field_management.repository.AccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class OwnerBookingController {

    private final BookingService bookingService;
    private final AccountRepository accountRepository;

    public OwnerBookingController(BookingService bookingService, AccountRepository accountRepository) {
        this.bookingService = bookingService;
        this.accountRepository = accountRepository;
    }

    @GetMapping("/owner/bookings")
    public String viewBookings(
            Model model,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String status) {

        Account owner = accountRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Page<Booking> bookingPage;

        if (status == null || status.equals("ALL")) {
            bookingPage = bookingService.getBookingsByOwner(owner.getEmail(), page);
        } else {
            bookingPage = bookingService.getBookingsByOwnerAndStatus(owner.getEmail(), status, page);
        }

        model.addAttribute("bookings", bookingPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookingPage.getTotalPages());
        model.addAttribute("status", status == null ? "ALL" : status);

        return "owner/bookings";
    }


    @GetMapping("/owner/bookings/today")
    public String viewTodaysBookings(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Account owner = accountRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        List<Booking> todaysBookings = bookingService.getTodaysBookingsByOwner(owner.getEmail());

        model.addAttribute("bookings", todaysBookings);
        model.addAttribute("today", LocalDate.now());

        return "owner/bookings_today";
    }
    @GetMapping("/owner/dashboard/home")
    public String ownerDashboard(Model model,
                                 @AuthenticationPrincipal UserDetails userDetails) {

        Account owner = accountRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Long todayCount = bookingService.countTodayBookingsByOwner(owner.getEmail());

        model.addAttribute("todayCount", todayCount);

        return "owner/home";
    }


    @GetMapping("/owner/bookings/approve")
    public String approveBooking(
            @RequestParam Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        String ownerEmail = userDetails.getUsername();
        bookingService.approveBooking(id, ownerEmail);

        model.addAttribute("successMessage", "Duyệt đặt sân thành công!");
        return "redirect:/owner/bookings";
    }

    @GetMapping("/owner/bookings/reject")
    public String rejectBooking(
            @RequestParam Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        String ownerEmail = userDetails.getUsername();
        bookingService.rejectBooking(id, ownerEmail);

        model.addAttribute("successMessage", "Từ chối yêu cầu đặt sân!");
        return "redirect:/owner/bookings";
    }





}
