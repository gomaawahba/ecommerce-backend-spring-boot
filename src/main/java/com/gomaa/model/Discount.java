package com.gomaa.model;

import com.gomaa.enums.DiscountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "discounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;              // SUMMER25
    private String name;              // Summer Sale 2026

    @Enumerated(EnumType.STRING)
    private DiscountType type;        // PERCENTAGE, FIXED, FREE_SHIPPING

    private Double value;             // 25%, 15$, null لو free shipping

    private Integer usageLimit;       // 500, 1000, null = unlimited
    private Integer usedCount;        // 142

    private LocalDate startDate;
    private LocalDate endDate;

    private Boolean active;            // enable / disable

    private LocalDateTime createdAt;
}

