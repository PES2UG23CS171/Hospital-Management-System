package com.hospital.management;

import com.hospital.management.model.Patient;
import com.hospital.management.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CRUD coverage for /patients.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(roles = "ADMIN")
class PatientCrudTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private PatientRepository patientRepository;

    private Patient persistPatient(String name) {
        Patient patient = new Patient();
        patient.setName(name);
        patient.setAge(30);
        patient.setGender("Female");
        patient.setPhone("9876543210");
        patient.setAddress("12 Residency Road");
        return patientRepository.save(patient);
    }

    @Test
    void listPageRendersPatients() throws Exception {
        persistPatient("Meera Rao");

        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(view().name("patients/list"))
                .andExpect(model().attributeExists("patients"));
    }

    @Test
    void newFormSuppliesEmptyPatient() throws Exception {
        mockMvc.perform(get("/patients/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("patients/form"))
                .andExpect(model().attributeExists("patient"));
    }

    @Test
    void createsPatient() throws Exception {
        mockMvc.perform(post("/patients/save").with(csrf())
                        .param("name", "Arjun Nair")
                        .param("age", "42")
                        .param("gender", "Male")
                        .param("phone", "9123456780")
                        .param("address", "8 Church Street"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients"));

        assertThat(patientRepository.findByNameContaining("Arjun"))
                .singleElement()
                .satisfies(p -> {
                    assertThat(p.getAge()).isEqualTo(42);
                    assertThat(p.getPhone()).isEqualTo("9123456780");
                });
    }

    @Test
    void editFormLoadsExistingPatient() throws Exception {
        Patient saved = persistPatient("Priya Menon");

        mockMvc.perform(get("/patients/edit/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("patients/form"))
                .andExpect(model().attribute("patient",
                        org.hamcrest.Matchers.hasProperty("name", org.hamcrest.Matchers.is("Priya Menon"))));
    }

    @Test
    void updatesExistingPatient() throws Exception {
        Patient saved = persistPatient("Rahul Shetty");

        mockMvc.perform(post("/patients/save").with(csrf())
                        .param("id", String.valueOf(saved.getId()))
                        .param("name", "Rahul Shetty")
                        .param("age", "55")
                        .param("gender", "Male")
                        .param("phone", "9000000000")
                        .param("address", "New Address"))
                .andExpect(redirectedUrl("/patients"));

        Patient updated = patientRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getAge()).isEqualTo(55);
        assertThat(updated.getAddress()).isEqualTo("New Address");
        assertThat(patientRepository.count()).isEqualTo(1);
    }

    @Test
    void deletesPatient() throws Exception {
        Patient saved = persistPatient("Temporary Patient");

        mockMvc.perform(get("/patients/delete/" + saved.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients"));

        assertThat(patientRepository.findById(saved.getId())).isEmpty();
    }
}
