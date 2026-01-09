package com.gomaa.controller;

import com.gomaa.dto.DiscountDTO;
import com.gomaa.service.DiscountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin("*")
public class DiscountController {

    private final DiscountService discountService;

    @Operation(summary = "Create a new discount", description = "Add a new discount to the system")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Discount created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping("/discounts")
    public ResponseEntity<DiscountDTO> create(@RequestBody DiscountDTO dto) {
        return ResponseEntity.ok(discountService.create(dto));
    }

    @Operation(summary = "Get all discounts", description = "Retrieve all discounts")
    @GetMapping("/discounts")
    public ResponseEntity<List<DiscountDTO>> getAll() {
        return ResponseEntity.ok(discountService.getAll());
    }

    @Operation(summary = "Get discount by ID", description = "Retrieve a single discount by ID")
    @GetMapping("/discounts/{id}")
    public ResponseEntity<DiscountDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(discountService.getById(id));
    }

    @Operation(summary = "Update a discount", description = "Update an existing discount by ID")
    @PutMapping("/discounts/{id}")
    public ResponseEntity<DiscountDTO> update(@PathVariable Long id, @RequestBody DiscountDTO dto) {
        return ResponseEntity.ok(discountService.update(id, dto));
    }

    @Operation(summary = "Delete a discount", description = "Delete a discount by ID")
    @DeleteMapping("/discounts/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        discountService.delete(id);
        return ResponseEntity.ok("Deleted successfully");
    }

    @Operation(summary = "Export discounts CSV", description = "Export all discounts to a CSV file")
    @GetMapping("/discounts/export")
    public void exportCsv(HttpServletResponse response) throws IOException {
        discountService.exportCsv(response);
    }

    @Operation(summary = "Search discounts", description = "Search discounts by keyword with pagination")
    @GetMapping("/discounts/search")
    public Page<DiscountDTO> search(@RequestParam String keyword,
                                    @RequestParam(defaultValue = "0") int page) {
        return discountService.search(keyword, page);
    }
}
