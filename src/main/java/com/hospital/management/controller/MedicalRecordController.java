package com.hospital.management.controller;

import com.hospital.management.model.MedicalRecord;

import com.hospital.management.model.Doctor;
import com.hospital.management.model.Patient;
import com.hospital.management.service.MedicalRecordService;
import com.hospital.management.service.PatientService;
import com.hospital.management.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/medical-records")
public class MedicalRecordController {
    
    @Autowired private MedicalRecordService medicalRecordService;
    @Autowired private PatientService patientService;
    @Autowired private DoctorService doctorService;
    
    @GetMapping
    public String list(Model model) {
        model.addAttribute("records", medicalRecordService.getAll());
        return "medical-records/list";
    }
    
    @GetMapping("/patient/{patientId}")
    public String listByPatient(@PathVariable Long patientId, Model model) {
        model.addAttribute("records", medicalRecordService.getByPatient(patientId));
        model.addAttribute("patient", patientService.getById(patientId));
        return "medical-records/list";
    }
    
    @GetMapping("/new")
    public String showForm(@RequestParam(required = false) Long patientId, Model model) {
        // Factory Pattern: create an empty shell for the form binding
        model.addAttribute("record", new MedicalRecord());
        model.addAttribute("patients", patientService.getAllPatients());
        model.addAttribute("doctors", doctorService.getAllDoctors());
        if (patientId != null) {
            model.addAttribute("selectedPatientId", patientId);
        }
        return "medical-records/form";
    }
    
    @PostMapping("/save")
    public String save(@RequestParam Long patientId,
                       @RequestParam(required = false) Long doctorId,
                       @RequestParam String diagnosis,
                       @RequestParam(required = false) String prescription) {
        // Factory Pattern: new MedicalRecord decides how to build the object
        Patient patient = patientService.getById(patientId);
        MedicalRecord record;
        if (doctorId != null) {
            Doctor doctor = doctorService.getDoctorById(doctorId).orElse(null);
            record = MedicalRecord.createRecord(patient, doctor, diagnosis, prescription);
        } else {
            record = MedicalRecord.createRecord(patient, diagnosis, prescription);
        }
        medicalRecordService.save(record);
        return "redirect:/medical-records/patient/" + patientId;
    }
    
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        MedicalRecord record = medicalRecordService.getById(id);
        Long patientId = record.getPatient().getId();
        medicalRecordService.delete(id);
        return "redirect:/medical-records/patient/" + patientId;
    }
}

