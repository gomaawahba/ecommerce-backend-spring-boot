package com.gomaa.controller;

import com.gomaa.dto.OrderDTO;
import com.gomaa.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Create a new order")
    @PostMapping("/orders")
    public ResponseEntity<OrderDTO> create(@RequestBody OrderDTO dto) {
        return ResponseEntity.ok(orderService.create(dto));
    }

    @Operation(summary = "Get all orders")
    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> getAll() {
        return ResponseEntity.ok(orderService.getAll());
    }

    @Operation(summary = "Get order by ID")
    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @Operation(summary = "Delete an order by ID")
    @DeleteMapping("/orders/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.ok("Deleted successfully");
    }

    @Operation(summary = "Export orders to CSV")
    @GetMapping("/orders/export")
    public void exportCsv(HttpServletResponse response) throws IOException {
        orderService.exportCsv(response);
    }
}
