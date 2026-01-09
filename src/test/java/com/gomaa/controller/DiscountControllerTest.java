package com.gomaa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gomaa.dto.DiscountDTO;
import com.gomaa.service.DiscountService;
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
class DiscountControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DiscountService discountService;

    @InjectMocks
    private DiscountController discountController;

    private ObjectMapper objectMapper;
    private DiscountDTO discountDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        objectMapper.findAndRegisterModules();

        mockMvc = MockMvcBuilders.standaloneSetup(discountController)
                .build();

        discountDTO = DiscountDTO.builder()
                .id(1L)
                .code("SUMMER25")
                .name("Summer Sale")
                .type("PERCENTAGE")
                .value(25.0)
                .usageLimit(100)
                .usedCount(10)
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(10))
                .active(true)
                .build();
    }


    @Test
    void createDiscount_success() throws Exception {
        when(discountService.create(any(DiscountDTO.class))).thenReturn(discountDTO);

        mockMvc.perform(post("/api/discounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(discountDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUMMER25"))
                .andExpect(jsonPath("$.name").value("Summer Sale"));
    }

    @Test
    void getDiscountById_success() throws Exception {
        when(discountService.getById(1L)).thenReturn(discountDTO);

        mockMvc.perform(get("/api/discounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUMMER25"));
    }

    @Test
    void getAllDiscounts_success() throws Exception {
        when(discountService.getAll()).thenReturn(List.of(discountDTO));

        mockMvc.perform(get("/api/discounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].code").value("SUMMER25"));
    }
}
