package com.hospital.management.controller;
import com.hospital.management.model.Bill;
import com.hospital.management.model.Patient;
import com.hospital.management.service.BillService;
import com.hospital.management.service.PatientService;
import com.hospital.management.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
/**
 * Controller for Billing operations.
 */
@Controller
@RequestMapping("/bills")
public class BillController {
    @Autowired
    private BillService billService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private AppointmentService appointmentService;
    @GetMapping
    public String listBills(Model model) {
        model.addAttribute("bills", billService.getAllBills());
        model.addAttribute("totalRevenue", billService.getTotalRevenue());
        return "bills/list";
    }
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("bill", new Bill());
        model.addAttribute("patients", patientService.getAllPatients());
        model.addAttribute("appointments",
            appointmentService.getAllAppointments());
        return "bills/form";
    }
    @PostMapping
    public String saveBill(@RequestParam Long patientId,
                           @RequestParam Double totalAmount,
                           @RequestParam(required = false) String itemsDescription) {
        Patient patient = patientService.getById(patientId);
        Bill bill = new Bill(patient, totalAmount, itemsDescription);
        billService.generateBill(bill);
        return "redirect:/bills";
    }
    /** Print bill details */
    @GetMapping("/print/{id}")
    public String printBill(@PathVariable Long id, Model model) {
        billService.getBillById(id).ifPresent(
            b -> model.addAttribute("bill", b));
        return "bills/print";
    }
    @GetMapping("/delete/{id}")
    public String deleteBill(@PathVariable Long id) {
        billService.deleteBill(id);
        return "redirect:/bills";
    }
}