package com.example.football_field_management.controller.users;

import com.example.football_field_management.dto.DistrictDTO;
import com.example.football_field_management.dto.VenueDTO;
import com.example.football_field_management.model.Venue;
import com.example.football_field_management.model.VenueImage;
import com.example.football_field_management.repository.VenueRepository;
import com.example.football_field_management.service.user.venue.IUserVenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
@CrossOrigin(origins = "http://localhost:3000")
public class HomeController {
    private final IUserVenueService venueService;
    private final VenueRepository venueRepository;

    @GetMapping
    public Page<VenueDTO> getVenues(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean status
    ) {
        return venueService.getAllVenues(page, size, keyword, status);
    }
    @GetMapping("/top5")
    public ResponseEntity<List<VenueDTO>> getTop5Venues() {
        List<Venue> topVenues = venueRepository.findTop5ByCompletedOrders(PageRequest.of(0, 5));

        List<VenueDTO> dtos = topVenues.stream().map(v -> {
            VenueDTO dto = new VenueDTO();
            dto.setVenueId(v.getVenueId());
            dto.setVenueName(v.getVenueName());
            dto.setAddress(v.getAddress());
            dto.setStatus(v.getStatus());

            // Lấy ảnh chính
            Optional<VenueImage> mainImage = v.getImages().stream()
                    .filter(VenueImage::isPrimary)
                    .findFirst();
            dto.setMainImagePath(mainImage.map(VenueImage::getPhotoPath).orElse(null));

            if (v.getDistrict() != null) {
                dto.setDistrict(new DistrictDTO(
                        v.getDistrict().getDistrict_id(),
                        v.getDistrict().getDistrict_name()
                ));
            }
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}

