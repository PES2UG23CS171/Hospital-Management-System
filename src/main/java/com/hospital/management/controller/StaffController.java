package com.hospital.management.controller;
import com.hospital.management.model.Staff;
import com.hospital.management.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
/**
 * Controller for Staff management.
 * Handles CRUD operations for hospital staff members.
 */
@Controller
@RequestMapping("/staff")
public class StaffController {
    @Autowired
    private StaffService staffService;
    @GetMapping
    public String listStaff(Model model) {
        model.addAttribute("staffList", staffService.getAllStaff());
        return "staff/list";
    }
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("staff", new Staff());
        return "staff/form";
    }
    @PostMapping
    public String saveStaff(@ModelAttribute Staff staff) {
        staffService.saveStaff(staff);
        return "redirect:/staff";
    }
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        staffService.getStaffById(id).ifPresent(
            s -> model.addAttribute("staff", s));
        return "staff/form";
    }
    @GetMapping("/delete/{id}")
    public String deleteStaff(@PathVariable Long id) {
        staffService.deleteStaff(id);
        return "redirect:/staff";
    }
}
