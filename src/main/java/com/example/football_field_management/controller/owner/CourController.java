package com.example.football_field_management.controller.owner;

import com.example.football_field_management.model.Cour;
import com.example.football_field_management.service.admin.venue.IVenueService;
import com.example.football_field_management.service.owner.cour.ICourService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cour")
@RequiredArgsConstructor
public class CourController {

    private final ICourService courService;
    private final IVenueService venueService;

    @GetMapping
    public String listCour(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("courId").descending());
        Page<Cour> courPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            courPage = courService.searchByName(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            courPage = courService.findAll(pageable);
        }

        model.addAttribute("cours", courPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", courPage.getTotalPages());
        model.addAttribute("totalItems", courPage.getTotalElements());

        return "owner/cour/list-cour";
    }




    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("cour", new Cour());
        model.addAttribute("venues", venueService.getList());
        return "owner/cour/form-cour";
    }


    @PostMapping("/create")
    public String createCour(@ModelAttribute Cour cour) {
        courService.save(cour);
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


    @PostMapping("/edit/{id}")
    public String updateCour(@PathVariable Long id, @ModelAttribute Cour cour) {
        courService.update(id, cour);
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
        Optional<Cour> cour = courService.findById(id);
        if (cour.isPresent()) {
            model.addAttribute("cour", cour.get());
            return "owner/cour/detail-cour";
        }
        return "redirect:/cour";
    }




}
