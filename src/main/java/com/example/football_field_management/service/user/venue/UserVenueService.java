package com.example.football_field_management.service.user.venue;

import com.example.football_field_management.dto.CourDTO;
import com.example.football_field_management.dto.DistrictDTO;
import com.example.football_field_management.dto.VenueDTO;
import com.example.football_field_management.dto.VenueImageDTO;
import com.example.football_field_management.model.Venue;
import com.example.football_field_management.model.VenueImage;
import com.example.football_field_management.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserVenueService implements IUserVenueService {

    private final VenueRepository venueRepository;

    @Override
    public Page<VenueDTO> getAllVenues(int page, int size, String keyword, Boolean status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("venueId").descending());
        Page<Venue> venues = venueRepository.findAllWithRelations(keyword, status, pageable);
        return venues.map(this::mapToDTO);
    }

    private VenueDTO mapToDTO(Venue venue) {
        VenueDTO dto = new VenueDTO();

        // --- Basic ---
        dto.setVenueId(venue.getVenueId());
        dto.setVenueName(venue.getVenueName());
        dto.setAddress(venue.getAddress());
        dto.setContactNumber(venue.getContactNumber());
        dto.setDescription(venue.getDescription());
        dto.setStatus(venue.getStatus());

        // --- District ---
        if (venue.getDistrict() != null) {
            DistrictDTO districtDTO = new DistrictDTO();
            districtDTO.setId(venue.getDistrict().getDistrict_id());
            districtDTO.setDistrictName(venue.getDistrict().getDistrict_name());
            dto.setDistrict(districtDTO);
        }

        // --- Owner ---
        if (venue.getOwner() != null) {
            dto.setOwnerId(venue.getOwner().getAccount_id());
            dto.setOwnerName(venue.getOwner().getFullName());
        }

        // --- Images ---
        List<VenueImageDTO> allImages = new ArrayList<>();
        if (venue.getImages() != null) {
            for (VenueImage img : venue.getImages()) {
                VenueImageDTO imgDTO = new VenueImageDTO();
                imgDTO.setPhotoId(img.getPhotoId());
                imgDTO.setPhotoPath(img.getPhotoPath());
                imgDTO.setPrimary(img.isPrimary());
                allImages.add(imgDTO);

                if (img.isPrimary()) {
                    dto.setMainImageId(img.getPhotoId());
                    dto.setMainImagePath(img.getPhotoPath());
                }
            }
        }
        dto.setImages(allImages);

        // --- Courts list (nếu bạn cần hiển thị chi tiết sân) ---
        if (venue.getCourts() != null) {
            List<CourDTO> courtDTOs = new ArrayList<>();
            venue.getCourts().forEach(c -> {
                CourDTO cDTO = new CourDTO(
                        c.getCourId(),
                        c.getCourName(),
                        c.getPricePerHour(),
                        c.getFieldSize(),
                        c.getLightsAvailable(),
                        c.getSurfaceType(),
                        c.getStatus()
                );
                courtDTOs.add(cDTO);
            });
            dto.setCourts(courtDTOs);
        }

        // --- Total courts (SỐ SÂN) ---
        int totalCourts = venueRepository.countCourtsByVenue(venue.getVenueId());
        dto.setTotalCourts(totalCourts);

        return dto;
    }
}
