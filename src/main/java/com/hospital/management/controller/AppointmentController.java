package com.hospital.management.controller;

import com.hospital.management.model.Appointment;
import com.hospital.management.model.Doctor;
import com.hospital.management.model.Patient;
import com.hospital.management.service.AppointmentService;
import com.hospital.management.service.DoctorService;
import com.hospital.management.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired private AppointmentService appointmentService;
    @Autowired private DoctorService doctorService;
    @Autowired private PatientService patientService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("appointments", appointmentService.getAll());
        return "appointments/list";
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("appointment", new Appointment());
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("patients", patientService.getAllPatients());
        return "appointments/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam Long doctorId,
                       @RequestParam Long patientId,
                       @ModelAttribute Appointment appointment) {
        Doctor doctor = doctorService.getDoctorById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        Patient patient = patientService.getById(patientId);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointmentService.save(appointment);
        return "redirect:/appointments";
    }

    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN')")
    @PostMapping("/{id}/schedule")
    public String schedule(@PathVariable Long id) {
        appointmentService.schedule(id);
        return "redirect:/appointments";
    }

    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN')")
    @PostMapping("/{id}/checkin")
    public String checkIn(@PathVariable Long id) {
        appointmentService.checkIn(id);
        return "redirect:/appointments";
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping("/{id}/start")
    public String startConsultation(@PathVariable Long id) {
        appointmentService.startConsultation(id);
        return "redirect:/appointments";
    }

    @PostMapping("/{id}/tests")
    public String orderTests(@PathVariable Long id) {
        appointmentService.orderTests(id);
        return "redirect:/appointments";
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping("/{id}/complete")
    public String complete(@PathVariable Long id) {
        appointmentService.complete(id);
        return "redirect:/appointments";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id) {
        appointmentService.cancel(id);
        return "redirect:/appointments";
    }
}
