package com.gomaa.service;

import com.gomaa.dto.NotificationDTO;
import com.gomaa.model.Customer;
import com.gomaa.model.Notification;
import com.gomaa.repository.CustomerRepository;
import com.gomaa.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final CustomerRepository customerRepository;

    public NotificationDTO create(NotificationDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Notification notification = Notification.builder()
                .title(dto.getTitle())
                .message(dto.getMessage())
                .customer(customer)
                .readStatus(false)
                .build();

        notificationRepository.save(notification);
        dto.setId(notification.getId());
        return dto;
    }

    public List<NotificationDTO> getByCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return notificationRepository.findByCustomer(customer)
                .stream()
                .map(n -> NotificationDTO.builder()
                        .id(n.getId())
                        .title(n.getTitle())
                        .message(n.getMessage())
                        .readStatus(n.isReadStatus())
                        .customerId(customerId)
                        .createdAt(n.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setReadStatus(true);
        notificationRepository.save(notification);
    }
}
