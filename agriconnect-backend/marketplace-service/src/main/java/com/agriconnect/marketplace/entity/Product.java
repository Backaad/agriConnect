package com.agriconnect.marketplace.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long farmerId;
    private String name;
    private String description;
    private String category;
    private BigDecimal price;
    private Double quantityAvailable;
    private LocalDate harvestDate;

    // Additional fields like images could be added here
}
