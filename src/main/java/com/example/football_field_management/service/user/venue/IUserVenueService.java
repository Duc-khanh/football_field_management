package com.example.football_field_management.service.user.venue;

import com.example.football_field_management.dto.VenueDTO;
import org.springframework.data.domain.Page;

public interface IUserVenueService {
    Page<VenueDTO> getAllVenues(int page, int size, String keyword, Boolean status);
}
