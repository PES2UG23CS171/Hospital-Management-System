package com.hospital.management;

import com.hospital.management.model.Appointment;
import com.hospital.management.model.Doctor;
import com.hospital.management.model.Patient;
import com.hospital.management.repository.AppointmentRepository;
import com.hospital.management.repository.BillRepository;
import com.hospital.management.repository.DoctorRepository;
import com.hospital.management.repository.PatientRepository;
import com.hospital.management.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The appointment state machine: valid transitions, rejected transitions,
 * and the bill generated when a consultation completes.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AppointmentWorkflowTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private AppointmentService appointmentService;
    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private BillRepository billRepository;

    private Appointment appointment;

    @BeforeEach
    void setUp() {
        Patient patient = new Patient();
        patient.setName("Workflow Patient");
        patient = patientRepository.save(patient);

        Doctor doctor = new Doctor();
        doctor.setName("Dr. Workflow");
        doctor = doctorRepository.save(doctor);

        appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(LocalDate.now().plusDays(1));
        appointment.setAppointmentTime(LocalTime.of(10, 30));
        appointment = appointmentRepository.save(appointment);
    }

    private Appointment reload() {
        return appointmentRepository.findById(appointment.getId()).orElseThrow();
    }

    @Test
    void newAppointmentStartsAsRequested() {
        assertThat(reload().getStatus()).isEqualTo(Appointment.AppointmentStatus.REQUESTED);
    }

    @Test
    void movesThroughTheFullConsultationLifecycle() {
        appointmentService.schedule(appointment.getId());
        assertThat(reload().getStatus()).isEqualTo(Appointment.AppointmentStatus.SCHEDULED);

        appointmentService.checkIn(appointment.getId());
        assertThat(reload().getStatus()).isEqualTo(Appointment.AppointmentStatus.CHECKED_IN);

        appointmentService.startConsultation(appointment.getId());
        assertThat(reload().getStatus()).isEqualTo(Appointment.AppointmentStatus.IN_CONSULTATION);

        appointmentService.complete(appointment.getId());
        assertThat(reload().getStatus()).isEqualTo(Appointment.AppointmentStatus.COMPLETED);
    }

    @Test
    void completingConsultationGeneratesBill() {
        appointmentService.schedule(appointment.getId());
        appointmentService.checkIn(appointment.getId());
        appointmentService.startConsultation(appointment.getId());
        appointmentService.complete(appointment.getId());

        assertThat(billRepository.findByPatientId(appointment.getPatient().getId()))
                .singleElement()
                .satisfies(bill -> {
                    assertThat(bill.getTotalAmount()).isEqualTo(500.00);
                    assertThat(bill.getItemsDescription()).isEqualTo("Consultation fee");
                });
    }

    @Test
    void cannotCheckInBeforeScheduling() {
        assertThatThrownBy(() -> appointmentService.checkIn(appointment.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("SCHEDULED");
    }

    @Test
    void cannotCompleteFromRequestedState() {
        assertThatThrownBy(() -> appointmentService.complete(appointment.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void cannotScheduleTwice() {
        appointmentService.schedule(appointment.getId());

        assertThatThrownBy(() -> appointmentService.schedule(appointment.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void cancelsScheduledAppointment() {
        appointmentService.schedule(appointment.getId());
        appointmentService.cancel(appointment.getId());

        assertThat(reload().getStatus()).isEqualTo(Appointment.AppointmentStatus.CANCELLED);
    }

    @Test
    void cannotCancelCompletedAppointment() {
        appointmentService.schedule(appointment.getId());
        appointmentService.checkIn(appointment.getId());
        appointmentService.startConsultation(appointment.getId());
        appointmentService.complete(appointment.getId());

        assertThatThrownBy(() -> appointmentService.cancel(appointment.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("COMPLETED");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void bookingEndpointCreatesAppointment() throws Exception {
        mockMvc.perform(post("/appointments/save").with(csrf())
                        .param("doctorId", String.valueOf(appointment.getDoctor().getId()))
                        .param("patientId", String.valueOf(appointment.getPatient().getId()))
                        .param("appointmentDate", LocalDate.now().plusDays(3).toString())
                        .param("appointmentTime", "14:00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/appointments"));

        assertThat(appointmentRepository.findAll()).hasSize(2);
    }

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    void receptionistMayScheduleThroughEndpoint() throws Exception {
        mockMvc.perform(post("/appointments/" + appointment.getId() + "/schedule").with(csrf()))
                .andExpect(status().is3xxRedirection());

        assertThat(reload().getStatus()).isEqualTo(Appointment.AppointmentStatus.SCHEDULED);
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void patientMayNotScheduleThroughEndpoint() throws Exception {
        mockMvc.perform(post("/appointments/" + appointment.getId() + "/schedule").with(csrf()))
                .andExpect(status().isForbidden());

        assertThat(reload().getStatus()).isEqualTo(Appointment.AppointmentStatus.REQUESTED);
    }
}
