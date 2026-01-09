package com.gomaa.controller;

import com.gomaa.dto.ReviewDTO;
import com.gomaa.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "Create a review for a product")
    @PostMapping
    public ResponseEntity<ReviewDTO> create(@RequestBody ReviewDTO dto) {
        return ResponseEntity.ok(reviewService.create(dto));
    }

    @Operation(summary = "Get all reviews for a product")
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewDTO>> getByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getByProduct(productId));
    }

    @Operation(summary = "Delete a review by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        reviewService.delete(id);
        return ResponseEntity.ok("Review deleted successfully");
    }
}
