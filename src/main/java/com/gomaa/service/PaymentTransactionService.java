package com.gomaa.service;

import com.gomaa.dto.PaymentTransactionRequestDTO;
import com.gomaa.dto.PaymentTransactionResponseDTO;

public interface PaymentTransactionService {

    PaymentTransactionResponseDTO createPayment(PaymentTransactionRequestDTO request);

    PaymentTransactionResponseDTO getByOrderId(Long orderId);
}
