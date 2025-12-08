package com.example.football_field_management.controller.users;

import com.example.football_field_management.dto.CourDTO;
import com.example.football_field_management.service.owner.cour.ICourService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cour")
@CrossOrigin(origins = "http://localhost:3000")
public class CourReactController {
    private final ICourService courService;

    @GetMapping("/venue/{venueId}")
    public List<CourDTO> getCoursByVenue(@PathVariable Long venueId) {
        return courService.findByVenueIdC(venueId);
    }
}
