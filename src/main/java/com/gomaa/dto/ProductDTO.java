package com.gomaa.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {

    private Long id;
    private String name;
    private String description;

    private double price;
    private int stock;

    private String imageUrl;

    private Long categoryId;
    private String categoryName;
}
