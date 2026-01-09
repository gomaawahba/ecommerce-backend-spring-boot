package com.gomaa.service;

import com.gomaa.dto.CartItemDTO;
import com.gomaa.model.CartItem;
import com.gomaa.model.Order;
import com.gomaa.model.Product;
import com.gomaa.repository.CartItemRepository;
import com.gomaa.repository.OrderRepository;
import com.gomaa.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartItemService cartItemService;

    private Order order;
    private Product product;
    private CartItemDTO itemDTO;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);

        product = new Product();
        product.setId(1L);

        itemDTO = new CartItemDTO();
        itemDTO.setProductId(1L);
        itemDTO.setQuantity(2);
        itemDTO.setPrice(100.0);

        cartItem = new CartItem();
        cartItem.setOrder(order);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setPrice(100.0);
    }

    @Test
    void saveAll_success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.saveAll(anyList())).thenReturn(List.of(cartItem));

        List<CartItem> saved = cartItemService.saveAll(List.of(itemDTO), 1L);

        assertEquals(1, saved.size());
        assertEquals(100.0, saved.get(0).getPrice());
        verify(cartItemRepository, times(1)).saveAll(anyList());
    }

    @Test
    void getByOrder_success() {
        when(cartItemRepository.findByOrderId(1L)).thenReturn(List.of(cartItem));

        List<CartItemDTO> items = cartItemService.getByOrder(1L);

        assertEquals(1, items.size());
        assertEquals(100.0, items.get(0).getPrice());
    }

    @Test
    void delete_success() {
        when(cartItemRepository.existsById(1L)).thenReturn(true);
        doNothing().when(cartItemRepository).deleteById(1L);

        assertDoesNotThrow(() -> cartItemService.delete(1L));
        verify(cartItemRepository, times(1)).deleteById(1L);
    }
}
