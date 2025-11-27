package com.example.football_field_management.service.owner.booking;

import com.example.football_field_management.model.Booking;
import com.example.football_field_management.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService{
    private final BookingRepository bookingRepository;
    @Override
    public Page<Booking> findAll(int page, int size, String keyword, String status) {
        Pageable pageable = PageRequest.of(page, size);

        return bookingRepository.searchOrder(keyword, status, pageable);
    }
}
