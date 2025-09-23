package com.example.football_field_management.service.owner.cour;


import com.example.football_field_management.model.Cour;
import com.example.football_field_management.service.IGeneraService;

import java.util.List;



import com.example.football_field_management.model.Cour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICourService extends IGeneraService<Cour> {
    Cour update(Long id, Cour cour);

    void delete(Long id);

    List<Cour> getList();

    List<Cour> findByVenueId(Long venueId);

    List<Cour> findByStatus(Boolean status);
    Page<Cour> findAll(Pageable pageable);

    Page<Cour> searchByName(String keyword, Pageable pageable);

    Page<Cour> findByStatus(Boolean status, Pageable pageable);
    Page<Cour> findByNameAndStatus(String keyword, Boolean status, Pageable pageable);

}

