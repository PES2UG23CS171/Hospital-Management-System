package com.hospital.management.service;

import com.hospital.management.model.Admin;
import com.hospital.management.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    
    @Autowired private AdminRepository adminRepository;
    @Autowired private UserService userService;
    
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }
    
    public Optional<Admin> getAdminById(Long id) {
        return adminRepository.findById(id);
    }
    
    public Admin getByUserId(Long userId) {
        return adminRepository.findByUserId(userId);
    }
    
    public Admin save(Admin admin) {
        return adminRepository.save(admin);
    }
    
    public void delete(Long id) {
        adminRepository.deleteById(id);
    }
    
    // Business methods from diagram
    public void manageUsers(Long adminId) {
        Admin admin = getAdminById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        admin.manageUsers();
    }
    
    public String generateReports(Long adminId) {
        Admin admin = getAdminById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        return admin.generateReports();
    }
}
