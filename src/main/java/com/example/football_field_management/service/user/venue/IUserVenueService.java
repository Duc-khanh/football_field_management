package com.example.football_field_management.service.user.venue;

import com.example.football_field_management.dto.VenueDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IUserVenueService {
    Page<VenueDTO> getAllVenues(int page, int size, String keyword, Boolean status);
    Page<VenueDTO> getAllVenues(int page, int size, String keyword, Long districtId, Boolean status);

    List<VenueDTO> getTop5Venues();
}
