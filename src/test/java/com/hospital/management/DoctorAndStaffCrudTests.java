package com.hospital.management;

import com.hospital.management.model.Doctor;
import com.hospital.management.model.Staff;
import com.hospital.management.repository.DoctorRepository;
import com.hospital.management.repository.StaffRepository;
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
 * CRUD coverage for /doctors and /staff.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(roles = "ADMIN")
class DoctorAndStaffCrudTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private StaffRepository staffRepository;

    @Test
    void createsDoctor() throws Exception {
        mockMvc.perform(post("/doctors").with(csrf())
                        .param("name", "Dr. Kavya Iyer")
                        .param("specialization", "Cardiology")
                        .param("phone", "9812345670")
                        .param("schedule", "Mon-Fri 09:00-17:00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/doctors"));

        assertThat(doctorRepository.findBySpecializationContainingIgnoreCase("cardio"))
                .singleElement()
                .satisfies(d -> assertThat(d.getName()).isEqualTo("Dr. Kavya Iyer"));
    }

    @Test
    void listsDoctors() throws Exception {
        mockMvc.perform(get("/doctors"))
                .andExpect(status().isOk())
                .andExpect(view().name("doctors/list"))
                .andExpect(model().attributeExists("doctors"));
    }

    @Test
    void updatesDoctorSpecialization() throws Exception {
        Doctor doctor = new Doctor();
        doctor.setName("Dr. Sameer Khan");
        doctor.setSpecialization("General");
        doctor = doctorRepository.save(doctor);

        mockMvc.perform(post("/doctors").with(csrf())
                        .param("id", String.valueOf(doctor.getId()))
                        .param("name", "Dr. Sameer Khan")
                        .param("specialization", "Neurology"))
                .andExpect(redirectedUrl("/doctors"));

        assertThat(doctorRepository.findById(doctor.getId()).orElseThrow().getSpecialization())
                .isEqualTo("Neurology");
        assertThat(doctorRepository.count()).isEqualTo(1);
    }

    @Test
    void deletesDoctor() throws Exception {
        Doctor doctor = new Doctor();
        doctor.setName("Dr. Temp");
        doctor = doctorRepository.save(doctor);

        mockMvc.perform(get("/doctors/delete/" + doctor.getId()))
                .andExpect(redirectedUrl("/doctors"));

        assertThat(doctorRepository.findById(doctor.getId())).isEmpty();
    }

    @Test
    void createsAndDeletesStaffMember() throws Exception {
        mockMvc.perform(post("/staff").with(csrf())
                        .param("name", "Anita Desai")
                        .param("role", "Nurse")
                        .param("phone", "9700000001")
                        .param("schedule", "Night shift"))
                .andExpect(redirectedUrl("/staff"));

        Staff created = staffRepository.findByRoleContainingIgnoreCase("nurse").get(0);
        assertThat(created.getName()).isEqualTo("Anita Desai");

        mockMvc.perform(get("/staff/delete/" + created.getId()))
                .andExpect(redirectedUrl("/staff"));

        assertThat(staffRepository.findById(created.getId())).isEmpty();
    }

    @Test
    void listsStaff() throws Exception {
        mockMvc.perform(get("/staff"))
                .andExpect(status().isOk())
                .andExpect(view().name("staff/list"))
                .andExpect(model().attributeExists("staffList"));
    }
}
