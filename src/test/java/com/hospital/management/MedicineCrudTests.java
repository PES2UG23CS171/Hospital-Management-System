package com.hospital.management;

import com.hospital.management.model.Medicine;
import com.hospital.management.repository.MedicineRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CRUD plus inventory behaviour for /medicines.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(roles = "ADMIN")
class MedicineCrudTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private MedicineRepository medicineRepository;

    private Medicine persistMedicine(String name, int stock, LocalDate expiry) {
        Medicine medicine = new Medicine();
        medicine.setName(name);
        medicine.setManufacturer("Cipla");
        medicine.setStockQuantity(stock);
        medicine.setPrice(45.50);
        medicine.setExpiryDate(expiry);
        return medicineRepository.save(medicine);
    }

    @Test
    void createsMedicine() throws Exception {
        mockMvc.perform(post("/medicines/save").with(csrf())
                        .param("name", "Paracetamol")
                        .param("manufacturer", "Cipla")
                        .param("stockQuantity", "120")
                        .param("price", "25.00")
                        .param("expiryDate", LocalDate.now().plusYears(1).toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/medicines"));

        assertThat(medicineRepository.findByNameContainingIgnoreCase("paracetamol"))
                .singleElement()
                .satisfies(m -> {
                    assertThat(m.getStockQuantity()).isEqualTo(120);
                    assertThat(m.getPrice()).isEqualTo(25.00);
                });
    }

    @Test
    void listsMedicines() throws Exception {
        persistMedicine("Amoxicillin", 40, LocalDate.now().plusMonths(8));

        mockMvc.perform(get("/medicines"))
                .andExpect(status().isOk())
                .andExpect(view().name("medicines/list"))
                .andExpect(model().attributeExists("medicines"));
    }

    @Test
    void updateStockAddsToExistingQuantity() throws Exception {
        Medicine medicine = persistMedicine("Ibuprofen", 10, LocalDate.now().plusYears(2));

        mockMvc.perform(post("/medicines/" + medicine.getId() + "/update-stock").with(csrf())
                        .param("quantity", "15"))
                .andExpect(status().is3xxRedirection());

        assertThat(medicineRepository.findById(medicine.getId()).orElseThrow().getStockQuantity())
                .isEqualTo(25);
    }

    @Test
    void checkStockMarksDepletedMedicineOutOfStock() throws Exception {
        Medicine medicine = persistMedicine("Cetirizine", 0, LocalDate.now().plusYears(1));

        mockMvc.perform(post("/medicines/" + medicine.getId() + "/check-stock").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/medicines"));

        assertThat(medicineRepository.findById(medicine.getId()).orElseThrow().getStatus())
                .isEqualTo(Medicine.MedicineStatus.OUT_OF_STOCK);
    }

    @Test
    void checkStockMarksPastDateMedicineExpired() throws Exception {
        Medicine medicine = persistMedicine("Old Syrup", 50, LocalDate.now().minusDays(1));

        mockMvc.perform(post("/medicines/" + medicine.getId() + "/check-stock").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/medicines"));

        assertThat(medicineRepository.findById(medicine.getId()).orElseThrow().getStatus())
                .isEqualTo(Medicine.MedicineStatus.EXPIRED);
    }

    @Test
    void lowStockQueryReturnsOnlyMedicinesAtOrBelowThreshold() {
        persistMedicine("Low Stock Item", 3, LocalDate.now().plusYears(1));
        persistMedicine("Well Stocked Item", 500, LocalDate.now().plusYears(1));

        assertThat(medicineRepository.findLowStockMedicines(10))
                .extracting(Medicine::getName)
                .containsExactly("Low Stock Item");
    }

    @Test
    void expiringSoonQueryFindsMedicinesPastCutoff() {
        persistMedicine("Expiring Soon", 20, LocalDate.now().plusDays(10));
        persistMedicine("Fresh Stock", 20, LocalDate.now().plusYears(3));

        assertThat(medicineRepository.findByExpiryDateBefore(LocalDate.now().plusDays(30)))
                .extracting(Medicine::getName)
                .containsExactly("Expiring Soon");
    }

    @Test
    void deletesMedicine() throws Exception {
        Medicine medicine = persistMedicine("Discontinued", 5, LocalDate.now().plusMonths(2));

        mockMvc.perform(get("/medicines/delete/" + medicine.getId()))
                .andExpect(redirectedUrl("/medicines"));

        assertThat(medicineRepository.findById(medicine.getId())).isEmpty();
    }
}
