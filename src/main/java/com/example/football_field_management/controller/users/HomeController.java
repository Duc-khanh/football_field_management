package com.example.football_field_management.controller.users;

import com.example.football_field_management.dto.CourDTO;
import com.example.football_field_management.dto.VenueDTO;
import com.example.football_field_management.dto.VenueImageDTO;
import com.example.football_field_management.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
public class HomeController {
    private final VenueRepository venueRepository;
    @GetMapping
    public List<VenueDTO> getAllVenues() {
        return venueRepository.findAll().stream().map(v -> {
            VenueDTO dto = new VenueDTO();
            dto.setVenueId(v.getVenueId());
            dto.setVenueName(v.getVenueName());
            dto.setAddress(v.getAddress());
            dto.setContactNumber(v.getContactNumber());
            dto.setDescription(v.getDescription());
            dto.setStatus(v.getStatus());
            dto.setDistrictId(v.getDistrict() != null ? v.getDistrict().getDistrict_id() : null);
            dto.setOwnerId(v.getOwner() != null ? v.getOwner().getAccount_id() : null);
            dto.setOwnerName(v.getOwner() != null ? v.getOwner().getFullName() : null);

            // ảnh chính
            v.getImages().stream()
                    .filter(img -> img.isPrimary())
                    .findFirst()
                    .ifPresent(img -> {
                        dto.setMainImageId(img.getPhotoId());
                        dto.setMainImagePath(img.getPhotoPath());
                    });

            // ảnh phụ
            dto.setImages(v.getImages().stream()
                    .map(img -> {
                        VenueImageDTO imgDto = new VenueImageDTO();
                        imgDto.setPhotoId(img.getPhotoId());
                        imgDto.setPhotoPath(img.getPhotoPath());
                        imgDto.setPrimary(img.isPrimary());
                        return imgDto;
                    })
                    .collect(Collectors.toList()));

            // danh sách sân con
            dto.setCourts(v.getCourts().stream()
                    .map(c -> {
                        CourDTO courDTO = new CourDTO();
                        courDTO.setCourId(c.getCour_id());
                        courDTO.setCourName(c.getCour_name());
                        courDTO.setPricePerHour(c.getPrice_per_hour());
                        courDTO.setFieldSize(c.getField_size());
                        courDTO.setLightsAvailable(c.getLights_available());
                        courDTO.setSurfaceType(c.getSurface_type());
                        courDTO.setStatus(String.valueOf(c.getStatus()));
                        return courDTO;
                    }).collect(Collectors.toList()));

            return dto;
        }).collect(Collectors.toList());
    }

}
