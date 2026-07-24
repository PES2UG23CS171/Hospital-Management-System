package com.hospital.management.service;

import com.hospital.management.model.Patient;
import com.hospital.management.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PatientService {
    @Autowired private PatientRepository patientRepository;

    public List<Patient> getAllPatients() { return patientRepository.findAll(); }
    public long countPatients() { return patientRepository.count(); }
    public Patient getById(Long id) {
        return patientRepository.findById(id)
               .orElseThrow(() -> new RuntimeException("Patient not found"));
    }
    public Patient save(Patient patient) { return patientRepository.save(patient); }
    public void delete(Long id) { patientRepository.deleteById(id); }
}
