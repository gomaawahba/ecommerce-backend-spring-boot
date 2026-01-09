package com.gomaa.controller;

import com.gomaa.dto.PaymentTransactionRequestDTO;
import com.gomaa.dto.PaymentTransactionResponseDTO;
import com.gomaa.service.PaymentTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentTransactionController {

    private final PaymentTransactionService paymentTransactionService;

    @Operation(summary = "Create a new payment transaction")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Payment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping
    public ResponseEntity<PaymentTransactionResponseDTO> createPayment(
            @RequestBody PaymentTransactionRequestDTO request) {

        PaymentTransactionResponseDTO response = paymentTransactionService.createPayment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get payment transaction by order ID")
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentTransactionResponseDTO> getPaymentByOrder(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(paymentTransactionService.getByOrderId(orderId));
    }
}
