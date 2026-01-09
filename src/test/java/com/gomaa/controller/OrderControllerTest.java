package com.gomaa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gomaa.dto.CartItemDTO;
import com.gomaa.dto.OrderDTO;
import com.gomaa.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private ObjectMapper objectMapper;
    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();

        orderDTO = OrderDTO.builder()
                .id(1L)
                .orderNumber("ORD-001")
                .customerId(1L)
                .date(LocalDate.now())
                .items(List.of(new CartItemDTO(1L, 2, 50)))
                .status("Processing")
                .paymentStatus("Paid")
                .wayPayment("Paypal")
                .build();
    }

    @Test
    void createOrder_success() throws Exception {
        when(orderService.create(any(OrderDTO.class))).thenReturn(orderDTO);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumber").value("ORD-001"))
                .andExpect(jsonPath("$.status").value("Processing"));
    }

    @Test
    void getOrderById_success() throws Exception {
        when(orderService.getById(1L)).thenReturn(orderDTO);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumber").value("ORD-001"))
                .andExpect(jsonPath("$.paymentStatus").value("Paid"));
    }
}
