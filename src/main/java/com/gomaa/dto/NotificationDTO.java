package com.gomaa.dto;

import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {

    private Long id;
    private String title;
    private String message;
    private boolean readStatus;
    private Long customerId;
    private Instant createdAt;
}
