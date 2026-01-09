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
public class CustomerDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private LocalDate joined;
    private String status; // Active, Inactive, Blocked
    private int ordersCount;
    private double totalSpent;
}
