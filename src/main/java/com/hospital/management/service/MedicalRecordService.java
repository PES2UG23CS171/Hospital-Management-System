package com.hospital.management.service;

import com.hospital.management.model.MedicalRecord;
import com.hospital.management.repository.MedicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class MedicalRecordService {
    
    @Autowired private MedicalRecordRepository medicalRecordRepository;
    
    public List<MedicalRecord> getAll() {
        return medicalRecordRepository.findAll();
    }
    
    public List<MedicalRecord> getByPatient(Long patientId) {
        return medicalRecordRepository.findByPatientId(patientId);
    }
    
    public MedicalRecord getById(Long id) {
        return medicalRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medical record not found"));
    }
    
    public MedicalRecord save(MedicalRecord record) {
        if (record.getRecordDate() == null) {
            record.setRecordDate(LocalDate.now());
        }
        return medicalRecordRepository.save(record);
    }
    
    public void delete(Long id) {
        medicalRecordRepository.deleteById(id);
    }
}
