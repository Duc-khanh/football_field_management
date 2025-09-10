package com.example.football_field_management.service.admin.venue;

import com.example.football_field_management.dto.VenueDTO;
import com.example.football_field_management.dto.VenueImageDTO;
import com.example.football_field_management.service.IGeneraService;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IVenueService extends IGeneraService<VenueDTO> {
    void changeStatus(Long id, boolean isOpen);
    void deleteImageById(Long imageId);
    VenueImageDTO getImageById(Long imageId);

    void deleteSubImagesNotInList(Long venueId, List<Long> keepIds);
    void remove(Long id);
    Page<VenueDTO> findAll(int page, int size, String keyword,Boolean status);
}
