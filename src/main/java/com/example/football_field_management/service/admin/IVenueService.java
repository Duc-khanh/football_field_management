package com.example.football_field_management.service.admin;

import com.example.football_field_management.dto.VenueDTO;
import com.example.football_field_management.dto.VenueImageDTO;
import com.example.football_field_management.model.Venue;
import com.example.football_field_management.service.IGeneraService;

import java.util.List;

public interface IVenueService extends IGeneraService<VenueDTO> {
    void changeStatus(Long id, boolean isOpen);
    List<Venue> searchByName(String name);
    void deleteImageById(Long imageId);
    VenueImageDTO getImageById(Long imageId);

    void deleteSubImagesNotInList(Long venueId, List<Long> keepIds);
    void remove(Long id);
}
