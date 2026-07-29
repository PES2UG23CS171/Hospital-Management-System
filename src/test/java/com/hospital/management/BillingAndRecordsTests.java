package com.hospital.management;

import com.hospital.management.model.Doctor;
import com.hospital.management.model.MedicalRecord;
import com.hospital.management.model.Patient;
import com.hospital.management.repository.BillRepository;
import com.hospital.management.repository.DoctorRepository;
import com.hospital.management.repository.MedicalRecordRepository;
import com.hospital.management.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
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
 * Billing (/bills) and medical record (/medical-records) flows.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(roles = "ADMIN")
class BillingAndRecordsTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private PatientRepository patientRepository;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private BillRepository billRepository;
    @Autowired private MedicalRecordRepository medicalRecordRepository;

    private Patient patient;
    private Doctor doctor;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setName("Bill Test Patient");
        patient.setAge(35);
        patient = patientRepository.save(patient);

        doctor = new Doctor();
        doctor.setName("Dr. Record Tester");
        doctor.setSpecialization("General");
        doctor = doctorRepository.save(doctor);
    }

    @Test
    void createsBillForPatient() throws Exception {
        mockMvc.perform(post("/bills").with(csrf())
                        .param("patientId", String.valueOf(patient.getId()))
                        .param("totalAmount", "1250.75")
                        .param("itemsDescription", "Consultation + X-Ray"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bills"));

        assertThat(billRepository.findByPatientId(patient.getId()))
                .singleElement()
                .satisfies(bill -> {
                    assertThat(bill.getTotalAmount()).isEqualTo(1250.75);
                    assertThat(bill.getItemsDescription()).isEqualTo("Consultation + X-Ray");
                    assertThat(bill.getBillDate()).isNotNull();
                });
    }

    @Test
    void billListExposesTotalRevenue() throws Exception {
        createBill(1000.00);
        createBill(500.00);

        mockMvc.perform(get("/bills"))
                .andExpect(status().isOk())
                .andExpect(view().name("bills/list"))
                .andExpect(model().attributeExists("bills"))
                .andExpect(model().attribute("totalRevenue", 1500.00));
    }

    @Test
    void printsBillReceipt() throws Exception {
        createBill(320.00);
        Long billId = billRepository.findByPatientId(patient.getId()).get(0).getId();

        mockMvc.perform(get("/bills/print/" + billId))
                .andExpect(status().isOk())
                .andExpect(view().name("bills/print"))
                .andExpect(model().attributeExists("bill"));
    }

    @Test
    void deletesBill() throws Exception {
        createBill(99.99);
        Long billId = billRepository.findByPatientId(patient.getId()).get(0).getId();

        mockMvc.perform(get("/bills/delete/" + billId))
                .andExpect(redirectedUrl("/bills"));

        assertThat(billRepository.findById(billId)).isEmpty();
    }

    @Test
    void createsMedicalRecordForPatient() throws Exception {
        mockMvc.perform(post("/medical-records/save").with(csrf())
                        .param("patientId", String.valueOf(patient.getId()))
                        .param("doctorId", String.valueOf(doctor.getId()))
                        .param("diagnosis", "Seasonal influenza")
                        .param("prescription", "Rest and fluids"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/medical-records/patient/" + patient.getId()));

        assertThat(medicalRecordRepository.findByPatientId(patient.getId()))
                .singleElement()
                .satisfies(record -> {
                    assertThat(record.getDiagnosis()).isEqualTo("Seasonal influenza");
                    assertThat(record.getDoctor().getId()).isEqualTo(doctor.getId());
                });
    }

    @Test
    void createsMedicalRecordWithoutDoctor() throws Exception {
        mockMvc.perform(post("/medical-records/save").with(csrf())
                        .param("patientId", String.valueOf(patient.getId()))
                        .param("diagnosis", "Routine checkup"))
                .andExpect(status().is3xxRedirection());

        assertThat(medicalRecordRepository.findByPatientId(patient.getId()))
                .singleElement()
                .satisfies(record -> assertThat(record.getDoctor()).isNull());
    }

    @Test
    void listsRecordsForOnePatientOnly() throws Exception {
        Patient other = new Patient();
        other.setName("Other Patient");
        other = patientRepository.save(other);

        medicalRecordRepository.save(MedicalRecord.createRecord(patient, "Migraine", "Painkillers"));
        medicalRecordRepository.save(MedicalRecord.createRecord(other, "Fracture", "Cast"));

        mockMvc.perform(get("/medical-records/patient/" + patient.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("medical-records/list"))
                .andExpect(model().attributeExists("records", "patient"));

        assertThat(medicalRecordRepository.findByPatientId(patient.getId())).hasSize(1);
        assertThat(medicalRecordRepository.findAll()).hasSize(2);
    }

    private void createBill(double amount) throws Exception {
        mockMvc.perform(post("/bills").with(csrf())
                        .param("patientId", String.valueOf(patient.getId()))
                        .param("totalAmount", String.valueOf(amount))
                        .param("itemsDescription", "Test charge"))
                .andExpect(status().is3xxRedirection());
    }
}
