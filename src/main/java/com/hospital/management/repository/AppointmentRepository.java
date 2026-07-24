package com.hospital.management.repository;
import com.hospital.management.model.Appointment;
import com.hospital.management.model.Appointment.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    /** Find appointments for a specific patient */
    List<Appointment> findByPatientId(Long patientId);
    /** Find appointments for a specific doctor */
    List<Appointment> findByDoctorId(Long doctorId);
    /** Find appointments by date */
    List<Appointment> findByAppointmentDate(LocalDate date);
    /** Find appointments by status */
    List<Appointment> findByStatus(AppointmentStatus status);
    /** Find appointments for a doctor on a specific date */
    List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate date);
}