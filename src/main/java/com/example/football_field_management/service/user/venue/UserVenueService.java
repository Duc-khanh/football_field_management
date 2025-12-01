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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserVenueService implements IUserVenueService {

    private final VenueRepository venueRepository;

    private VenueDTO mapToDTO(Venue venue) {
        VenueDTO dto = new VenueDTO();

        // --- Basic info ---
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

        // --- Courts ---
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

        // --- Total courts ---
        int totalCourts = venueRepository.countCourtsByVenue(venue.getVenueId());
        dto.setTotalCourts(totalCourts);

        // --- Price (lấy sân đầu tiên nếu có) ---
        if (venue.getCourts() != null && !venue.getCourts().isEmpty()) {
            dto.setPrice(venue.getCourts().get(0).getPricePerHour());
        }

        return dto;
    }

    @Override
    public Page<VenueDTO> getAllVenues(int page, int size, String keyword, Boolean status) {
        return null;
    }

    @Override
    public Page<VenueDTO> getAllVenues(int page, int size, String keyword, Long districtId, Boolean status) {
        PageRequest pageable = PageRequest.of(page, size);

        Page<Venue> venues = venueRepository.searchVenues(
                keyword != null && !keyword.isEmpty() ? keyword : null,
                districtId,
                status,
                pageable
        );

        return venues.map(this::mapToDTO);
    }

    @Override
    public List<VenueDTO> getTop5Venues() {
        List<Venue> topVenues = venueRepository.findTop5ByCompletedOrders(PageRequest.of(0, 5));
        List<VenueDTO> dtos = new ArrayList<>();
        for (Venue v : topVenues) {
            dtos.add(mapToDTO(v));
        }
        return dtos;
    }
}
