package com.hospital.management.service;

import com.hospital.management.model.Medicine;
import com.hospital.management.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class MedicineService {
    @Autowired private MedicineRepository medicineRepository;

    public List<Medicine> getAll() { return medicineRepository.findAll(); }
    public List<Medicine> getAllMedicines() { return medicineRepository.findAll(); }

    public Medicine getById(Long id) {
        return medicineRepository.findById(id)
               .orElseThrow(() -> new RuntimeException("Medicine not found: " + id));
    }

    public Medicine save(Medicine m) { return medicineRepository.save(m); }

    public Medicine.MedicineStatus checkStock(Long medicineId) {
        Medicine m = getById(medicineId);
        m.updateStatus();
        medicineRepository.save(m);
        return m.getStatus();
    }

    public void updateStock(Long medicineId, int quantity) {
        Medicine m = getById(medicineId);
        m.setStockQuantity(m.getStockQuantity() + quantity);
        medicineRepository.save(m);
    }

    public List<Medicine> getLowStock(int threshold) {
        return medicineRepository.findLowStockMedicines(threshold);
    }

    public List<Medicine> getExpiringSoon() {
        return medicineRepository.findByExpiryDateBefore(LocalDate.now().plusDays(30));
    }

    public void delete(Long id) { medicineRepository.deleteById(id); }
}
