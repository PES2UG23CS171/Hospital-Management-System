package com.hospital.management.controller;
import com.hospital.management.model.Admin;
import com.hospital.management.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
/**
 * Admin Controller - provides admin dashboard with reports and stats.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired private AdminService adminService;
    @Autowired private PatientService patientService;
    @Autowired private DoctorService doctorService;
    @Autowired private StaffService staffService;
    @Autowired private MedicineService medicineService;
    @Autowired private AppointmentService appointmentService;
    @Autowired private BillService billService;
    /** Admin dashboard with overview statistics */
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("patientCount", patientService.countPatients());
        model.addAttribute("doctorCount",
            doctorService.getAllDoctors().size());
        model.addAttribute("staffCount",
            staffService.getAllStaff().size());
        model.addAttribute("medicineCount",
            medicineService.getAllMedicines().size());
        model.addAttribute("totalRevenue",
            billService.getTotalRevenue());
        model.addAttribute("lowStockMedicines",
            medicineService.getLowStock(10));
        model.addAttribute("expiringSoon",
            medicineService.getExpiringSoon());
        return "admin/dashboard";
    }
    /** Generate reports */
    @GetMapping("/reports")
    public String generateReports(Model model) {
        model.addAttribute("appointments",
            appointmentService.getAllAppointments());
        model.addAttribute("bills", billService.getAllBills());
        model.addAttribute("totalRevenue",
            billService.getTotalRevenue());
        return "admin/reports";
    }
    
    /** List all admins */
    @GetMapping("/list")
    public String listAdmins(Model model) {
        model.addAttribute("admins", adminService.getAllAdmins());
        return "admin/list";
    }
    
    /** Show admin form */
    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("admin", new Admin());
        return "admin/form";
    }
    
    /** Save admin */
    @PostMapping("/save")
    public String save(@ModelAttribute Admin admin) {
        adminService.save(admin);
        return "redirect:/admin/list";
    }
    
    /** Delete admin */
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        adminService.delete(id);
        return "redirect:/admin/list";
    }
}
