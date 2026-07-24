package com.hospital.management.controller;

import com.hospital.management.model.Medicine;
import com.hospital.management.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/medicines")
public class MedicineController {

    @Autowired private MedicineService medicineService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("medicines", medicineService.getAll());
        return "medicines/list";
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("medicine", new Medicine());
        return "medicines/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        medicineService.delete(id);
        return "redirect:/medicines";
    }

    @PostMapping("/{id}/check-stock")
    public String checkStock(@PathVariable Long id, Model model) {
        var status = medicineService.checkStock(id);
        model.addAttribute("stockStatus", status);
        return "redirect:/medicines";
    }

    @PostMapping("/{id}/update-stock")
    public String updateStock(@PathVariable Long id, @RequestParam int quantity) {
        medicineService.updateStock(id, quantity);
        return "redirect:/medicines";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Medicine medicine) {
        medicineService.save(medicine);
        return "redirect:/medicines";
    }
}
