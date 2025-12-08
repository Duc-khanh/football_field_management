package com.example.football_field_management.controller.owner;

import com.example.football_field_management.model.Account;
import com.example.football_field_management.model.Cour;
import com.example.football_field_management.repository.AccountRepository;
import com.example.football_field_management.service.admin.venue.IVenueService;
import com.example.football_field_management.service.owner.cour.ICourService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;


@Controller
@RequestMapping("/cour")
@RequiredArgsConstructor
public class CourController {

    private final ICourService courService;
    private final IVenueService venueService;
    private final AccountRepository accountRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;


    @GetMapping
    public String listCour(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Boolean status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,

            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("courId").descending());
        Page<Cour> courPage;

        if (keyword != null && !keyword.trim().isEmpty() && status != null) {
            courPage = courService.findByNameAndStatus(keyword, status, pageable);
        }
        else if (keyword != null && !keyword.trim().isEmpty()) {
            courPage = courService.searchByName(keyword, pageable);
        }
        else if (status != null) {
            courPage = courService.findByStatus(status, pageable);
        }
        else {
            courPage = courService.findAll(pageable);
        }

        model.addAttribute("cours", courPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", courPage.getTotalPages());
        model.addAttribute("totalItems", courPage.getTotalElements());

        // giữ lại keyword & status để hiển thị đúng form
        model.addAttribute("keyword", keyword);
        model.addAttribute("statusFilter", status);

        return "owner/cour/list-cour";
    }




    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("cour", new Cour());
        model.addAttribute("venues", venueService.getList());
        return "owner/cour/form-cour";
    }


    @PostMapping("/create")
    public String createCour(@ModelAttribute Cour cour,
                             @RequestParam("file") MultipartFile file,
                             RedirectAttributes redirectAttributes) {

        if (!file.isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                File dest = new File(uploadDir + File.separator + fileName);
                file.transferTo(dest);
                cour.setImage(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // ✅ SET OWNER
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Account owner = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy account"));

        cour.setOwner(owner);

        courService.save(cour);

        redirectAttributes.addFlashAttribute("successMessage", "Thêm sân bóng thành công!");
        return "redirect:/cour";
    }


    @PostMapping("/edit/{id}")
    public String updateCour(@PathVariable Long id,
                             @ModelAttribute Cour cour,
                             RedirectAttributes redirectAttributes,
                             @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        // Lấy bản ghi cũ để giữ thông tin không bị mất
        Cour oldCour = courService.findById(id).orElseThrow();

        if (file != null && !file.isEmpty()) {
            // Tạo tên file duy nhất
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir, fileName);

            // Lưu file vào thư mục upload
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            // Set ảnh mới
            cour.setImage(fileName);
        } else {
            // Không upload ảnh mới → giữ ảnh cũ
            cour.setImage(oldCour.getImage());
        }

        // Giữ lại các trường không sửa (nếu form không có)
        cour.setCourId(oldCour.getCourId());
//        cour.setVenue(oldCour.getVenue()); // nếu bạn không cho sửa venue trong form

        courService.update(id, cour);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật sân bóng thành công!");
        return "redirect:/cour";
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Cour> cour = courService.findById(id);
        if (cour.isPresent()) {
            model.addAttribute("cour", cour.get());
            model.addAttribute("venues", venueService.getList());
            return "owner/cour/form-cour";
        }
        return "redirect:/cour";
    }



    @GetMapping("/delete/{id}")
    public String deleteCour(@PathVariable Long id) {
        courService.delete(id);
        return "redirect:/cour";
    }

    @GetMapping("/status/{status}")
    public String filterByStatus(@PathVariable Boolean status, Model model) {
        List<Cour> cours = courService.findByStatus(status);
        model.addAttribute("cours", cours);
        model.addAttribute("statusFilter", status);
        return "owner/cour/list-cour";
    }


    @GetMapping("/detail/{id}")
    public String viewDetail(@PathVariable Long id, Model model) {
        Cour cour = courService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sân với id: " + id));
        model.addAttribute("cour", cour);
        return "owner/cour/detail-cour";
    }

}