package com.hospital.management.repository;
import com.hospital.management.model.Medicine;
import com.hospital.management.model.Medicine.MedicineStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    /** Find medicines by name */
    List<Medicine> findByNameContainingIgnoreCase(String name);
    /** Find medicines by status (IN_STOCK, OUT_OF_STOCK, EXPIRED) */
    List<Medicine> findByStatus(MedicineStatus status);
    /** Find medicines expiring before a given date */
    List<Medicine> findByExpiryDateBefore(LocalDate date);
    /** Find medicines with low stock (below threshold) */
    @Query("SELECT m FROM Medicine m WHERE m.stockQuantity <= :threshold AND m.status != 'EXPIRED'")
    List<Medicine> findLowStockMedicines(int threshold);
}