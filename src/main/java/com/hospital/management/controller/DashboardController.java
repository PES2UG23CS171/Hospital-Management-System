package com.hospital.management.controller;

import com.hospital.management.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired private PatientService patientService;
    @Autowired private DoctorService doctorService;
    @Autowired private StaffService staffService;
    @Autowired private MedicineService medicineService;
    @Autowired private BillService billService;
    @Autowired private AppointmentService appointmentService;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        try {
            model.addAttribute("patientCount", patientService.countPatients());
            model.addAttribute("doctorCount", doctorService.getAllDoctors().size());
            model.addAttribute("staffCount", staffService.getAllStaff().size());
            model.addAttribute("medicineCount", medicineService.getAllMedicines().size());
            Double revenue = billService.getTotalRevenue();
            model.addAttribute("totalRevenue", revenue != null ? revenue : 0.0);
            model.addAttribute("lowStockMedicines", medicineService.getLowStock(10));
            model.addAttribute("expiringSoon", medicineService.getExpiringSoon());
            model.addAttribute("recentAppointments", appointmentService.getAllAppointments());
        } catch (Exception e) {
            // Handle any errors gracefully
            model.addAttribute("patientCount", 0);
            model.addAttribute("doctorCount", 0);
            model.addAttribute("staffCount", 0);
            model.addAttribute("medicineCount", 0);
            model.addAttribute("totalRevenue", 0.0);
        }
        return "dashboard";
    }
}
