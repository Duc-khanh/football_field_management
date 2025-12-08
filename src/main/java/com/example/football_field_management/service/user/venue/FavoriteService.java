package com.example.football_field_management.service.user.venue;

import com.example.football_field_management.model.Favorite;
import com.example.football_field_management.repository.AccountRepository;
import com.example.football_field_management.repository.FavoriteRepository;
import com.example.football_field_management.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepo;
    private final VenueRepository venueRepo;
    private final AccountRepository accountRepo;

    // Trả về true nếu đã thêm, false nếu đã xóa
    public boolean toggleFavorite(Long userId, Long venueId) {
        boolean exists = favoriteRepo.existsFavorite(userId, venueId);

        if (exists) {
            favoriteRepo.deleteFavorite(userId, venueId);
            return false; // Đã xóa khỏi danh sách
        } else {
            Favorite f = new Favorite();
            f.setUser(accountRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found")));
            f.setVenue(venueRepo.findById(venueId).orElseThrow(() -> new RuntimeException("Venue not found")));
            favoriteRepo.save(f);
            return true; // Đã thêm vào danh sách
        }
    }
}