package com.example.football_field_management.repository;

import com.example.football_field_management.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

        @Query("SELECT COUNT(f) > 0 FROM Favorite f WHERE f.user.account_id = :accountId AND f.venue.venueId = :venueId")
        boolean existsFavorite(@Param("accountId") Long accountId, @Param("venueId") Long venueId);

        // ✅ Cần thêm @Transactional để thực hiện thao tác DELETE
        @Modifying
        @Transactional
        @Query("DELETE FROM Favorite f WHERE f.user.account_id = :accountId AND f.venue.venueId = :venueId")
        void deleteFavorite(@Param("accountId") Long accountId, @Param("venueId") Long venueId);

        @Query("SELECT f FROM Favorite f WHERE f.user.account_id = :accountId")
        List<Favorite> findFavoritesByAccountId(@Param("accountId") Long accountId);
}