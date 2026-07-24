package com.hospital.management.service;

import com.hospital.management.model.Appointment;
import com.hospital.management.model.Bill;
import com.hospital.management.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BillService {
    @Autowired private BillRepository billRepository;

    public Bill generateBill(Appointment appointment) {
        Bill bill = new Bill();
        bill.setPatient(appointment.getPatient());
        bill.setAppointment(appointment);
        bill.setBillDate(LocalDate.now());
        bill.setTotalAmount(500.00);
        bill.setItemsDescription("Consultation fee");
        return billRepository.save(bill);
    }

    public Bill generateBill(Bill bill) { return billRepository.save(bill); }
    public List<Bill> getAllBills() { return billRepository.findAll(); }
    public Optional<Bill> getBillById(Long id) { return billRepository.findById(id); }
    public Double getTotalRevenue() { return billRepository.calculateTotalRevenue(); }
    public void deleteBill(Long id) { billRepository.deleteById(id); }
}
