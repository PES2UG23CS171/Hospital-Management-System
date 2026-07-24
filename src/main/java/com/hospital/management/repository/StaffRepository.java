package com.hospital.management.repository;

import com.hospital.management.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    List<Staff> findByRoleContainingIgnoreCase(String role);
    Staff findByUserId(Long userId);
}
