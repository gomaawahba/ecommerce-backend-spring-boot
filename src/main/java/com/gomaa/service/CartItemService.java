package com.gomaa.service;

import com.gomaa.dto.CartItemDTO;
import com.gomaa.model.CartItem;
import com.gomaa.model.Order;
import com.gomaa.model.Product;
import com.gomaa.repository.CartItemRepository;
import com.gomaa.repository.OrderRepository;
import com.gomaa.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public CartItem toEntity(CartItemDTO dto, Order order) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem item = new CartItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(dto.getQuantity());
        item.setPrice(dto.getPrice());
        return item;
    }

    public CartItemDTO toDto(CartItem item) {
        CartItemDTO dto = new CartItemDTO();
        dto.setProductId(item.getProduct().getId());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        return dto;
    }

    public List<CartItemDTO> getByOrder(Long orderId) {
        return cartItemRepository.findByOrderId(orderId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<CartItem> saveAll(List<CartItemDTO> items, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        List<CartItem> entityItems = items.stream()
                .map(dto -> toEntity(dto, order))
                .toList();
        return cartItemRepository.saveAll(entityItems);
    }

    public void delete(Long id) {
        if (!cartItemRepository.existsById(id)) {
            throw new RuntimeException("CartItem not found");
        }
        cartItemRepository.deleteById(id);
    }
}
