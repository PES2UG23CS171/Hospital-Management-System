package com.hospital.management.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "medicines")
public class Medicine {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100) private String name;
    @Column(length = 100) private String manufacturer;
    @Column(name = "stock_quantity") private Integer stockQuantity = 0;
    private Double price;
    @Column(name = "expiry_date") private LocalDate expiryDate;
    @Enumerated(EnumType.STRING) private MedicineStatus status = MedicineStatus.IN_STOCK;

    public enum MedicineStatus { IN_STOCK, OUT_OF_STOCK, EXPIRED }

    public Medicine() {}
    public Medicine(String name, String manufacturer, Integer stockQuantity,
                    Double price, LocalDate expiryDate) {
        this.name = name; this.manufacturer = manufacturer;
        this.stockQuantity = stockQuantity; this.price = price;
        this.expiryDate = expiryDate; updateStatus();
    }

    public void updateStatus() {
        if (expiryDate != null && expiryDate.isBefore(LocalDate.now())) {
            this.status = MedicineStatus.EXPIRED;
        } else if (stockQuantity == null || stockQuantity <= 0) {
            this.status = MedicineStatus.OUT_OF_STOCK;
        } else {
            this.status = MedicineStatus.IN_STOCK;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String m) { this.manufacturer = m; }
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer sq) { this.stockQuantity = sq; updateStatus(); }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate ed) { this.expiryDate = ed; updateStatus(); }
    public MedicineStatus getStatus() { return status; }
    public void setStatus(MedicineStatus status) { this.status = status; }
}
