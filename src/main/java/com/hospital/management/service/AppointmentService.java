package com.hospital.management.service;

import com.hospital.management.model.Appointment;
import com.hospital.management.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AppointmentService {

    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private BillService billService;

    public List<Appointment> getAll() { return appointmentRepository.findAll(); }
    public List<Appointment> getAllAppointments() { return appointmentRepository.findAll(); }

    public Appointment getById(Long id) {
        return appointmentRepository.findById(id)
               .orElseThrow(() -> new RuntimeException("Appointment not found: " + id));
    }

    public Appointment save(Appointment a) { return appointmentRepository.save(a); }

    public void schedule(Long id) {
        Appointment a = getById(id); a.schedule(); appointmentRepository.save(a);
    }
    public void checkIn(Long id) {
        Appointment a = getById(id); a.checkIn(); appointmentRepository.save(a);
    }
    public void startConsultation(Long id) {
        Appointment a = getById(id); a.startConsultation(); appointmentRepository.save(a);
    }
    public void orderTests(Long id) {
        Appointment a = getById(id); a.orderTests(); appointmentRepository.save(a);
    }
    public void complete(Long id) {
        Appointment a = getById(id); a.complete(); appointmentRepository.save(a);
        billService.generateBill(a);
    }
    public void cancel(Long id) {
        Appointment a = getById(id); a.cancel(); appointmentRepository.save(a);
    }
    public void delete(Long id) { appointmentRepository.deleteById(id); }
}
