package com.hospital.management.service;
import com.hospital.management.model.Staff;
import com.hospital.management.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
/**
 * Service class for Staff management.
 * Handles staff CRUD operations and schedule viewing.
 */
@Service
public class StaffService {
    @Autowired
    private StaffRepository staffRepository;
    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }
    public Optional<Staff> getStaffById(Long id) {
        return staffRepository.findById(id);
    }
    public List<Staff> findByRole(String role) {
        return staffRepository.findByRoleContainingIgnoreCase(role);
    }
    public Staff saveStaff(Staff staff) {
        return staffRepository.save(staff);
    }
    public void deleteStaff(Long id) {
        staffRepository.deleteById(id);
    }
}