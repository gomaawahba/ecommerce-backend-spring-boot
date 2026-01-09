package com.gomaa.controller;

import com.gomaa.dto.NotificationDTO;
import com.gomaa.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Create a notification for a customer")
    @PostMapping
    public ResponseEntity<NotificationDTO> create(@RequestBody NotificationDTO dto) {
        return ResponseEntity.ok(notificationService.create(dto));
    }

    @Operation(summary = "Get all notifications for a customer")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<NotificationDTO>> getByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(notificationService.getByCustomer(customerId));
    }

    @Operation(summary = "Mark a notification as read")
    @PutMapping("/{id}/read")
    public ResponseEntity<String> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok("Notification marked as read");
    }
}
