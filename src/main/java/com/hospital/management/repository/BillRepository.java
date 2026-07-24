package com.hospital.management.repository;
import com.hospital.management.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    /** Find all bills for a patient */
    List<Bill> findByPatientId(Long patientId);
    /** Calculate total revenue */
    @Query("SELECT SUM(b.totalAmount) FROM Bill b")
    Double calculateTotalRevenue();
}
