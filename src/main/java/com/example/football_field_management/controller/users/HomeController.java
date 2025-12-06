package com.example.football_field_management.controller.users;

import com.example.football_field_management.dto.CourDTO;
import com.example.football_field_management.dto.DistrictDTO;
import com.example.football_field_management.dto.VenueDTO;
import com.example.football_field_management.dto.VenueImageDTO;
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
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Boolean status
    ) {
        return venueService.getAllVenues(page, size, keyword, districtId, status);
    }
    @GetMapping("/top5")
    public ResponseEntity<List<VenueDTO>> getTop5Venues() {
        return ResponseEntity.ok(venueService.getTop5Venues());
    }




    // ✅ CHI TIẾT VENUE
    @GetMapping("/{venueId}")
    public ResponseEntity<VenueDTO> getVenueById(@PathVariable Long venueId) {

        Optional<Venue> venueOpt = venueRepository.findById(venueId);

        if (venueOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Venue v = venueOpt.get();

        VenueDTO dto = new VenueDTO();
        dto.setVenueId(v.getVenueId());
        dto.setVenueName(v.getVenueName());
        dto.setAddress(v.getAddress());
        dto.setStatus(v.getStatus());
        dto.setDescription(v.getDescription());
        dto.setContactNumber(v.getContactNumber());

        // Ảnh chính
        Optional<VenueImage> mainImage = v.getImages().stream()
                .filter(VenueImage::isPrimary)
                .findFirst();
        dto.setMainImagePath(mainImage.map(VenueImage::getPhotoPath).orElse(null));

        // District
        if (v.getDistrict() != null) {
            dto.setDistrict(new DistrictDTO(
                    v.getDistrict().getDistrict_id(),
                    v.getDistrict().getDistrict_name()
            ));
        }

        // Courts
        if (v.getCourts() != null && !v.getCourts().isEmpty()) {
            dto.setCourts(v.getCourts().stream()
                    .map(c -> new CourDTO(
                            c.getCourId(),
                            c.getCourName(),
                            c.getFieldSize(),
                            c.getPricePerHour()
                    ))
                    .toList()
            );
            dto.setTotalCourts(v.getCourts().size());
            dto.setPrice(v.getCourts().get(0).getPricePerHour());
        } else {
            dto.setTotalCourts(0);
            dto.setPrice(0);
        }

        // Ảnh phụ
        dto.setImages(v.getImages().stream()
                .map(img -> {
                    VenueImageDTO imageDTO = new VenueImageDTO();
                    imageDTO.setPhotoId(img.getPhotoId());
                    imageDTO.setPhotoPath(img.getPhotoPath());
                    imageDTO.setPrimary(img.isPrimary());
                    return imageDTO;
                })
                .toList()
        );

        // Owner
        if (v.getOwner() != null) {
            dto.setOwnerId(v.getOwner().getAccount_id());
            dto.setOwnerName(v.getOwner().getFullName());
        }

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/search")
    public ResponseEntity<List<VenueDTO>> searchVenues(
            @RequestParam String keyword
    ) {
        List<Venue> venues = venueRepository.searchVenues(keyword);

        List<VenueDTO> dtos = venues.stream().map(v -> {
            VenueDTO dto = new VenueDTO();
            dto.setVenueId(v.getVenueId());
            dto.setVenueName(v.getVenueName());
            dto.setAddress(v.getAddress());
            dto.setStatus(v.getStatus());

            // Main Image
            Optional<VenueImage> mainImage = v.getImages().stream()
                    .filter(VenueImage::isPrimary)
                    .findFirst();
            dto.setMainImagePath(mainImage.map(VenueImage::getPhotoPath).orElse(null));

            // District
            if (v.getDistrict() != null) {
                dto.setDistrict(new DistrictDTO(
                        v.getDistrict().getDistrict_id(),
                        v.getDistrict().getDistrict_name()
                ));
            }

            // Courts
            if (v.getCourts() != null && !v.getCourts().isEmpty()) {
                dto.setTotalCourts(v.getCourts().size());
                dto.setPrice(v.getCourts().get(0).getPricePerHour());
            } else {
                dto.setTotalCourts(0);
                dto.setPrice(0);
            }

            return dto;
        }).toList();

        return ResponseEntity.ok(dtos);
    }

}


