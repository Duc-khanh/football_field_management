package com.example.football_field_management.controller.users;

import com.example.football_field_management.dto.VenueDTO;
import com.example.football_field_management.service.admin.venue.IVenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
@CrossOrigin(origins = "http://localhost:3000")
public class HomeController {
    private final IVenueService venueService;

    @GetMapping
    public Page<VenueDTO> getVenues(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean status
    ) {
        return venueService.findAll(page, size, keyword, status);
    }
}

