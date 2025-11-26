package com.example.football_field_management.dto;

import com.example.football_field_management.model.District;
import com.example.football_field_management.model.Account; // ❗️ SỬA 1: Đổi UserEntity thành Account
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor // ❗️ BẮT BUỘC CÓ: Thêm constructor rỗng cho Jackson
public class VenueDTO {
    private Long venueId;
    private String venueName;
    private String address;
    private String contactNumber;
    private String description;
    private Boolean status;

    // --- Sửa để khớp với React ---
    private DistrictDTO district; // ❗️ Dùng DTO
    private Long ownerId;
    private String ownerName;

    // --- Giữ lại để logic save() của bạn hoạt động ---
    private Long districtId;

    // --- Các trường khác ---
    private Long mainImageId;
    private String mainImagePath;
    private MultipartFile mainImageFile;

    private List<VenueImageDTO> images;
    private List<Long> existingImageIds;
    private List<MultipartFile> subImagesFiles;
    private List<CourDTO> courts;
    private int totalCourts;
    private double price;

    // ⭐️ SỬA 2: Constructor đầy đủ để mapping (NẾU CẦN)
    // (Hiện tại logic mapToDTO trong service đang không dùng constructor này
    // nên bạn có thể xóa nó nếu muốn, nhưng giữ lại cũng không sao)
    public VenueDTO(Long venueId, String venueName, String address,
                    String contactNumber, String description, Boolean status,
                    District districtEntity,
                    Account ownerEntity, // ❗️ SỬA: Đổi UserEntity thành Account
                    List<VenueImageDTO> imageDTOs,
                    int totalCourts) {


        this.venueId = venueId;
        this.venueName = venueName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.description = description;
        this.status = status;
        this.totalCourts = totalCourts;

        // ❗️ SỬA: Dùng getter chính xác
        if (districtEntity != null) {
            this.district = new DistrictDTO(districtEntity.getDistrict_id(),
                    districtEntity.getDistrict_name());
        }

        // ❗️ SỬA: Dùng getter chính xác
        if (ownerEntity != null) {
            this.ownerId = ownerEntity.getAccount_id();
            this.ownerName = ownerEntity.getFullName();
        }

        this.images = imageDTOs;
    }

}