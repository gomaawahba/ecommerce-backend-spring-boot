package com.gomaa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gomaa.dto.CartItemDTO;
import com.gomaa.service.CartItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CartItemControllerTest {

    @Mock
    private CartItemService cartItemService;

    @InjectMocks
    private CartItemController cartItemController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private CartItemDTO itemDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(cartItemController).build();

        itemDTO = CartItemDTO.builder()
                .productId(1L)
                .quantity(2)
                .price(100.0)
                .build();
    }

    @Test
    void getByOrder_success() throws Exception {
        when(cartItemService.getByOrder(1L)).thenReturn(List.of(itemDTO));

        mockMvc.perform(get("/api/cart-items/order/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].price").value(100.0));
    }

    @Test
    void createForOrder_success() throws Exception {
        when(cartItemService.saveAll(List.of(itemDTO), 1L)).thenReturn(List.of());

        mockMvc.perform(post("/api/cart-items/order/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(List.of(itemDTO))))
                .andExpect(status().isOk());
    }

    @Test
    void delete_success() throws Exception {
        mockMvc.perform(delete("/api/cart-items/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Cart item deleted successfully"));
    }
}
