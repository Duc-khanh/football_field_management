package com.example.football_field_management.service.owner.cour;

import com.example.football_field_management.model.Cour;
import com.example.football_field_management.repository.CourRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourServiceImpl implements ICourService {

    private final CourRepository courRepository;

    public List<Cour> getList() {
        try {
            List<Cour> cours = courRepository.findAll();

            if (cours == null || cours.isEmpty()) {
                // Có thể log hoặc ném ngoại lệ tuỳ theo yêu cầu
                System.out.println("⚠ Không tìm thấy sân bóng nào trong cơ sở dữ liệu.");
                return new ArrayList<>(); // Trả về list rỗng thay vì null
            }

            return cours;
        } catch (Exception e) {
            // Ghi log để tiện debug
            System.err.println("❌ Lỗi khi lấy danh sách sân: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(); // Tránh NullPointerException cho tầng gọi
        }
    }



    @Override
    public Iterable<Cour> findAll() {
        return courRepository.findAll();
    }

    @Override
    public Optional<Cour> findById(Long id) {
        return courRepository.findById(id);
    }

    @Override
    public void save(Cour cour) {
        courRepository.save(cour);
    }

    @Override
    public void remote(Long id) {
        courRepository.deleteById(id);
    }

    @Override
    public Cour update(Long id, Cour cour) {
        Optional<Cour> existing = courRepository.findById(id);
        if (existing.isPresent()) {
            Cour updateCour = existing.get();
            // Giả sử class Cour có các field: name, status, price, venue,...
            updateCour.setCourName(cour.getCourName());
            updateCour.setPricePerHour(cour.getPricePerHour());
            updateCour.setFieldSize(cour.getFieldSize());
            updateCour.setSurfaceType(cour.getSurfaceType());
            updateCour.setLightsAvailable(cour.getLightsAvailable());
            updateCour.setStatus(cour.getStatus());
            updateCour.setVenue(cour.getVenue());
            return courRepository.save(updateCour);
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        courRepository.deleteById(id);
    }



    @Override
    public List<Cour> findByStatus(Boolean status) {
        return courRepository.findByStatus(status);
    }

    @Override
    public Page<Cour> findAll(Pageable pageable) {
        return courRepository.findAll(pageable);
    }

    @Override
    public Page<Cour> searchByName(String keyword, Pageable pageable) {
        return courRepository.findByCourNameContainingIgnoreCase(keyword, pageable);
    }


    @Override
    public List<Cour> findByVenueId(Long venueId) {
        return courRepository.findByVenue_VenueId(venueId);
    }
}
