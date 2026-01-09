package com.gomaa.controller;

import com.gomaa.dto.CartItemDTO;
import com.gomaa.service.CartItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;

    @Operation(summary = "Get all cart items for an order", description = "Retrieve all cart items associated with a specific order ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart items retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/cart-items/order/{orderId}")
    public ResponseEntity<List<CartItemDTO>> getByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(cartItemService.getByOrder(orderId));
    }

    @Operation(summary = "Add multiple cart items to an order", description = "Create multiple cart items for a given order ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart items created successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PostMapping("/cart-items/order/{orderId}")
    public ResponseEntity<List<CartItemDTO>> createForOrder(
            @PathVariable Long orderId,
            @RequestBody List<CartItemDTO> items) {
        return ResponseEntity.ok(
                cartItemService.saveAll(items, orderId)
                        .stream()
                        .map(cartItemService::toDto)
                        .toList()
        );
    }

    @Operation(summary = "Delete a cart item", description = "Delete a specific cart item by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart item deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Cart item not found")
    })
    @DeleteMapping("/cart-items/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        cartItemService.delete(id);
        return ResponseEntity.ok("Cart item deleted successfully");
    }
}
