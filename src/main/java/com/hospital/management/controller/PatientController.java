package com.hospital.management.controller;

import com.hospital.management.model.Patient;

import com.hospital.management.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/patients")
public class PatientController {
    @Autowired private PatientService patientService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("patients", patientService.getAllPatients());
        return "patients/list";
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        // Factory Pattern: create a new patient using factory method
        model.addAttribute("patient", new Patient());
        return "patients/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Patient patient) {
        patientService.save(patient);
        return "redirect:/patients";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        patientService.delete(id);
        return "redirect:/patients";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        patientService.getById(id);
        model.addAttribute("patient", patientService.getById(id));
        return "patients/form";
    }
}
