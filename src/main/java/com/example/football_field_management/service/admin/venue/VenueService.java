package com.example.football_field_management.service.admin.venue;

import com.example.football_field_management.dto.DistrictDTO;
import com.example.football_field_management.dto.VenueDTO;
import com.example.football_field_management.dto.VenueImageDTO;
import com.example.football_field_management.model.Venue;
import com.example.football_field_management.model.VenueImage;
import com.example.football_field_management.repository.AccountRepository;
import com.example.football_field_management.repository.DistrictRepository;
import com.example.football_field_management.repository.VenueImageRepository;
import com.example.football_field_management.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VenueService implements IVenueService {

    private final VenueRepository venueRepository;
    private final DistrictRepository districtRepository;
    private final AccountRepository accountRepository;
    private final VenueImageRepository venueImageRepository;

    private final String uploadDir = "uploads";

    @Override
    public Page<VenueDTO> findAll(int page, int size, String keyword, Boolean status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("venueId").descending());

        Page<Venue> venues;

        // (Logic tìm kiếm của bạn giữ nguyên - Đã đúng)
        if (keyword != null && !keyword.trim().isEmpty() && status != null) {
            venues = venueRepository.findByVenueNameContainingIgnoreCaseAndStatus(keyword, status, pageable);
        }
        else if (keyword != null && !keyword.trim().isEmpty()) {
            venues = venueRepository.findByVenueNameContainingIgnoreCase(keyword, pageable);
        }
        else if (status != null) {
            venues = venueRepository.findByStatus(status, pageable);
        }
        else {
            venues = venueRepository.findAll(pageable);
        }

        return venues.map(this::mapToDTO);
    }


    @Override
    public Iterable<VenueDTO> findAll() {
        return null; // Giữ nguyên
    }

    @Override
    public Optional<VenueDTO> findById(Long id) {
        // Phương thức này RẤT QUAN TRỌNG, nó dùng cho trang chi tiết
        return venueRepository.findById(id).map(this::mapToDTO);
    }

    @Transactional
    @Override
    public void save(VenueDTO venueDTO) {
        Venue venue = venueDTO.getVenueId() != null
                ? venueRepository.findById(venueDTO.getVenueId()).orElse(new Venue())
                : new Venue();

        venue.setVenueName(venueDTO.getVenueName());
        venue.setAddress(venueDTO.getAddress());
        venue.setContactNumber(venueDTO.getContactNumber());
        venue.setDescription(venueDTO.getDescription());
        venue.setStatus(venueDTO.getStatus());

        if (venueDTO.getDistrict() != null && venueDTO.getDistrict().getId() != null) {
            districtRepository.findById(venueDTO.getDistrict().getId()).ifPresent(venue::setDistrict);
        } else if (venueDTO.getDistrictId() != null) {
            // Hỗ trợ nếu DTO cũ vẫn gửi districtId
            districtRepository.findById(venueDTO.getDistrictId()).ifPresent(venue::setDistrict);
        }

        if (venueDTO.getOwnerId() != null) {
            accountRepository.findById(venueDTO.getOwnerId()).ifPresent(venue::setOwner);
        }

        Venue savedVenue = venueRepository.save(venue);

        // (Logic xử lý ảnh của bạn giữ nguyên)
        if (venueDTO.getImages() != null && !venueDTO.getImages().isEmpty()) {

            List<VenueImage> currentImages = savedVenue.getImages() != null
                    ? new ArrayList<>(savedVenue.getImages())
                    : new ArrayList<>();

            Optional<VenueImage> oldMain = currentImages.stream().filter(VenueImage::isPrimary).findFirst();
            oldMain.ifPresent(img -> {
                venueImageRepository.delete(img);
                deleteFile(img.getPhotoPath());
            });

            List<Long> keepIds = venueDTO.getImages().stream()
                    .filter(img -> img.getPhotoId() != null)
                    .map(VenueImageDTO::getPhotoId)
                    .collect(Collectors.toList());

            for (VenueImage img : currentImages) {
                if (!img.isPrimary() && !keepIds.contains(img.getPhotoId())) {
                    venueImageRepository.delete(img);
                    deleteFile(img.getPhotoPath());
                }
            }

            List<VenueImage> toSave = venueDTO.getImages().stream()
                    .map(dto -> {
                        VenueImage img = new VenueImage();
                        img.setVenue(savedVenue);
                        img.setPhotoPath(dto.getPhotoPath());
                        img.setPrimary(dto.isPrimary());
                        return img;
                    })
                    .collect(Collectors.toList());

            venueImageRepository.saveAll(toSave);
        }
    }

    @Override
    public void remote(Long id) {
        // Giữ nguyên
    }

    @Transactional
    @Override
    public void deleteImageById(Long imageId) {
        venueImageRepository.findById(imageId).ifPresent(img -> {
            venueImageRepository.delete(img);
            deleteFile(img.getPhotoPath());
        });
    }

    @Override
    public VenueImageDTO getImageById(Long imageId) {
        return venueImageRepository.findById(imageId).map(img -> {
            VenueImageDTO dto = new VenueImageDTO();
            dto.setPhotoId(img.getPhotoId());
            dto.setPhotoPath(img.getPhotoPath());
            dto.setPrimary(img.isPrimary());
            return dto;
        }).orElse(null);
    }

    @Transactional
    @Override
    public void deleteSubImagesNotInList(Long venueId, List<Long> keepIds) {
        List<VenueImage> images = venueImageRepository.findByVenue_VenueId(venueId);
        for (VenueImage img : images) {
            if (!img.isPrimary() && !keepIds.contains(img.getPhotoId())) {
                venueImageRepository.delete(img);
                deleteFile(img.getPhotoPath());
            }
        }
    }

    private void deleteFile(String filename) {
        try {
            if (filename != null) {
                Path filePath = Paths.get(uploadDir).resolve(filename);
                Files.deleteIfExists(filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(Long id) {
        venueRepository.findById(id).ifPresent(venue -> {
            if (venue.getImages() != null) {
                venue.getImages().forEach(img -> deleteFile(img.getPhotoPath()));
                venueImageRepository.deleteAll(venue.getImages());
            }
            venueRepository.delete(venue);
        });
    }

    @Override
    public List<Venue> getList() {
        return venueRepository.findAll();
    }

    @Override
    public void changeStatus(Long id, boolean isOpen) {
        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Địa điểm không tồn tại"));
        venue.setStatus(isOpen);
        venueRepository.save(venue);
    }

    private VenueDTO mapToDTO(Venue venue) {
        VenueDTO dto = new VenueDTO();
        dto.setVenueId(venue.getVenueId());
        dto.setVenueName(venue.getVenueName());
        dto.setAddress(venue.getAddress());
        dto.setContactNumber(venue.getContactNumber());
        dto.setDescription(venue.getDescription());
        dto.setStatus(venue.getStatus());

        if (venue.getDistrict() != null) {
            // dto.setDistrictId(venue.getDistrict().getDistrict_id()); // <-- BỎ DÒNG NÀY

            // Tạo một DTO mới cho district
            DistrictDTO districtDTO = new DistrictDTO();
            districtDTO.setId(venue.getDistrict().getDistrict_id());
            districtDTO.setDistrictName(venue.getDistrict().getDistrict_name()); // Giả sử tên trường là district_name

            dto.setDistrict(districtDTO); // <-- THAY BẰNG DÒNG NÀY
        }

        // (Phần Owner đã đúng)
        if (venue.getOwner() != null) {
            dto.setOwnerId(venue.getOwner().getAccount_id());
            dto.setOwnerName(venue.getOwner().getFullName());
        }

        List<VenueImageDTO> allImages = new ArrayList<>(); // Tạo list mới
        if (venue.getImages() != null) {
            for (VenueImage img : venue.getImages()) {
                VenueImageDTO imgDTO = new VenueImageDTO();
                imgDTO.setPhotoId(img.getPhotoId());
                imgDTO.setPhotoPath(img.getPhotoPath());
                imgDTO.setPrimary(img.isPrimary());

                allImages.add(imgDTO); // <-- Thêm TẤT CẢ ảnh (cả chính và phụ)

                // Vẫn giữ logic set ảnh chính để dùng ở nơi khác nếu cần
                if (img.isPrimary()) {
                    dto.setMainImageId(img.getPhotoId());
                    dto.setMainImagePath(img.getPhotoPath());
                }
            }
        }
        dto.setImages(allImages); // <-- Gán danh sách ĐẦY ĐỦ
        return dto;
    }

}