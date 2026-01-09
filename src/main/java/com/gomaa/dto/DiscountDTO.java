package com.gomaa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscountDTO {

    private Long id;

    private String code;        // SUMMER25
    private String name;        // Summer Sale 2026

    private String type;        // PERCENTAGE, FIXED_AMOUNT, FREE_SHIPPING
    private Double value;       // 25%, 15$, null لو free shipping

    private Integer usageLimit; // 500, null = unlimited
    private Integer usedCount;

    private LocalDate startDate;
    private LocalDate endDate;

    private Boolean active;

    private String status;      // ACTIVE, EXPIRING_SOON, EXPIRED
}
