package com.example.football_field_management.controller.admin;

import com.example.football_field_management.dto.VenueDTO;
import com.example.football_field_management.dto.VenueImageDTO;
import com.example.football_field_management.model.Venue;
import com.example.football_field_management.model.VenueImage;
import com.example.football_field_management.service.admin.IVenueService;
import com.example.football_field_management.repository.AccountRepository;
import com.example.football_field_management.repository.DistrictRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/venue")
@RequiredArgsConstructor
public class VenueController {

    private final IVenueService venueService;
    private final DistrictRepository districtRepository;
    private final AccountRepository accountRepository;
    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping
    public String listVenues(Model model) {
        Iterable<VenueDTO> venues = venueService.findAll();
        model.addAttribute("venues", venues);
        return "admin/venue/venue-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("venue", new VenueDTO());
        model.addAttribute("districts", districtRepository.findAll());
        model.addAttribute("owners", accountRepository.findAll()); // lọc chủ sân nếu cần
        return "admin/venue/venue-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        VenueDTO venueDTO = venueService.findById(id)
                .orElseThrow(() -> new RuntimeException("Venue not found"));

        if (venueDTO.getImages() == null) {
            venueDTO.setImages(new ArrayList<>());
        }

        model.addAttribute("venue", venueDTO);
        model.addAttribute("districts", districtRepository.findAll());
        model.addAttribute("owners", accountRepository.findAll());
        return "admin/venue/venue-form";
    }

    @PostMapping("/save")
    public String saveVenue(@ModelAttribute VenueDTO venueDTO,
                            @RequestParam(value = "mainImageFile", required = false) MultipartFile mainImageFile,
                            @RequestParam(value = "subImagesFiles", required = false) MultipartFile[] subImageFiles,
                            @RequestParam(value = "existingMainImageId", required = false) Long existingMainImageId,
                            @RequestParam(value = "existingImageIds", required = false) List<Long> existingImageIds) throws IOException {

        // 1. Xử lý ảnh chính
        VenueImageDTO mainImage = null;
        if (mainImageFile != null && !mainImageFile.isEmpty()) {
            String path = saveFile(mainImageFile);
            mainImage = new VenueImageDTO();
            mainImage.setPhotoPath(path);
            mainImage.setPrimary(true);

            // Xóa ảnh chính cũ nếu có
            if (existingMainImageId != null) {
                venueService.deleteImageById(existingMainImageId);
            }

        } else if (existingMainImageId != null) {
            // Lấy ảnh chính cũ từ DB
            mainImage = venueService.getImageById(existingMainImageId);
        }

        // 2. Xử lý ảnh phụ
        List<VenueImageDTO> images = new ArrayList<>();
        if (mainImage != null) images.add(mainImage);

        // Xóa các ảnh phụ đã bị remove
        List<Long> keepIds = existingImageIds != null ? existingImageIds : new ArrayList<>();
        venueService.deleteSubImagesNotInList(venueDTO.getVenueId(), keepIds);

        // Thêm ảnh phụ còn giữ
        if (existingImageIds != null) {
            for (Long id : existingImageIds) {
                VenueImageDTO dto = venueService.getImageById(id);
                if (dto != null) images.add(dto);
            }
        }

        // Thêm ảnh phụ mới
        if (subImageFiles != null) {
            for (MultipartFile f : subImageFiles) {
                if (!f.isEmpty()) {
                    VenueImageDTO dto = new VenueImageDTO();
                    dto.setPhotoPath(saveFile(f));
                    dto.setPrimary(false);
                    images.add(dto);
                }
            }
        }

        venueDTO.setImages(images);

        // 3. Gọi service lưu venue
        venueService.save(venueDTO);

        return "redirect:/admin/venue";
    }

    private String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        Path dir = Paths.get(uploadDir);
        if (!Files.exists(dir)) Files.createDirectories(dir);

        String ext = "";
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf("."));
        }

        String filename = UUID.randomUUID() + ext;
        Path filePath = dir.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return filename;
    }


    @GetMapping("/status/{id}/{isOpen}")
    public String changeVenueStatus(@PathVariable("id") Long id,
                                    @PathVariable("isOpen") boolean isOpen,
                                    RedirectAttributes redirectAttributes) {
        try {
            venueService.changeStatus(id, isOpen);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/venue";
    }
    @GetMapping("/search")
    public String searchVenue(@RequestParam(value = "keyword") String keyword,
                             Model model) {
        List<Venue> venues = venueService.searchByName(keyword);
        model.addAttribute("venues", venues);
        model.addAttribute("keyword", keyword);
        return "admin/venue/venue-list";
    }

}
