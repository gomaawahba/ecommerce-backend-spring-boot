package com.gomaa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gomaa.dto.CustomerDTO;
import com.gomaa.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Support LocalDate serialization

        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();

        customerDTO = CustomerDTO.builder()
                .id(1L)
                .name("gomaa wahba")
                .email("john@example.com")
                .phone("123456789")
                .joined(LocalDate.now())
                .status("ACTIVE")
                .build();
    }

    @Test
    void createCustomer_success() throws Exception {
        when(customerService.create(customerDTO)).thenReturn(customerDTO);

        mockMvc.perform(post("/api/customers")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("gomaa wahba"));
    }

    @Test
    void getAllCustomers_success() throws Exception {
        when(customerService.getAll()).thenReturn(List.of(customerDTO));

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("gomaa wahba"));
    }

    @Test
    void getCustomerById_success() throws Exception {
        when(customerService.getById(1L)).thenReturn(customerDTO);

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("gomaa wahba"));
    }

    @Test
    void updateCustomer_success() throws Exception {
        when(customerService.update(1L, customerDTO)).thenReturn(customerDTO);

        mockMvc.perform(put("/api/customers/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("gomaa wahba"));
    }
}
