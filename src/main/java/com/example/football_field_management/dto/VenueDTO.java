package com.example.football_field_management.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter @Setter
public class VenueDTO {
    private Long venueId;
    private String venueName;
    private String address;
    private String contactNumber;
    private String description;
    private Boolean status;
    private Long districtId;
    private Long ownerId;
    private String ownerName;
    private List<String> imagePaths;
    private List<VenueImageDTO> images;
    private List<Long> existingImageIds;
    private MultipartFile mainImageFile;
    private List<MultipartFile> subImagesFiles;
}
