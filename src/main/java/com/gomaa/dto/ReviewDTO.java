package com.gomaa.dto;

import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {
    private Long id;
    private String comment;
    private int rating;
    private Long customerId;
    private Long productId;
    private Instant createdAt;
}
