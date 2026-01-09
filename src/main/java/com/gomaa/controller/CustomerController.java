package com.gomaa.controller;

import com.gomaa.dto.CustomerDTO;
import com.gomaa.service.CustomerService;
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
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "Create a customer", description = "Add a new customer with all details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping("/customers")
    public ResponseEntity<CustomerDTO> create(@RequestBody CustomerDTO dto) {
        return ResponseEntity.ok(customerService.create(dto));
    }

    @Operation(summary = "Get all customers", description = "Retrieve all customers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customers retrieved successfully")
    })
    @GetMapping("/customers")
    public ResponseEntity<List<CustomerDTO>> getAll() {
        return ResponseEntity.ok(customerService.getAll());
    }

    @Operation(summary = "Get customer by ID", description = "Retrieve a single customer by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping("/customers/{id}")
    public ResponseEntity<CustomerDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getById(id));
    }

    @Operation(summary = "Update customer", description = "Update customer details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @PutMapping("/customers/{id}")
    public ResponseEntity<CustomerDTO> update(@PathVariable Long id, @RequestBody CustomerDTO dto) {
        return ResponseEntity.ok(customerService.update(id, dto));
    }

    @Operation(summary = "Delete customer", description = "Delete a customer by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @DeleteMapping("/customers/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.ok("Deleted successfully");
    }

    @Operation(summary = "Export customers CSV", description = "Export all customer data to a CSV file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CSV exported successfully")
    })
    @GetMapping("/customers/export")
    public void exportCsv(HttpServletResponse response) throws IOException {
        customerService.exportCsv(response);
    }
}
