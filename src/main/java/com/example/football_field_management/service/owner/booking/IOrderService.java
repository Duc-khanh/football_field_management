package com.example.football_field_management.service.owner.booking;

import com.example.football_field_management.model.Booking;
import com.example.football_field_management.service.IGeneraService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IOrderService {
    Page<Booking> findAll(int page, int size, String keyword, String status);
}
