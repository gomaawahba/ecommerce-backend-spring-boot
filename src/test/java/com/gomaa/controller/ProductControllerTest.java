package com.gomaa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gomaa.dto.ProductDTO;
import com.gomaa.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ObjectMapper objectMapper;

    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();

        productDTO = ProductDTO.builder()
                .id(1L)
                .name("Laptop")
                .description("Gaming Laptop")
                .price(1500)
                .stock(5)
                .imageUrl("img.png")
                .categoryId(1L)
                .categoryName("Electronics")
                .build();
    }

    @Test
    void getAllProducts_success() throws Exception {
        when(productService.getAll()).thenReturn(List.of(productDTO));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[0].price").value(1500));
    }

    @Test
    void getProductById_success() throws Exception {
        when(productService.getById(1L)).thenReturn(productDTO);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    void getProductsByCategory_success() throws Exception {
        when(productService.getByCategory(1L)).thenReturn(List.of(productDTO));

        mockMvc.perform(get("/api/products/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].categoryName").value("Electronics"));
    }

    @Test
    void searchProducts_success() throws Exception {
        when(productService.searchByName("Laptop")).thenReturn(List.of(productDTO));

        mockMvc.perform(get("/api/products/search")
                        .param("name", "Laptop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Laptop"));
    }

    @Test
    void createProduct_success() throws Exception {
        when(productService.create(any(ProductDTO.class))).thenReturn(productDTO);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.categoryName").value("Electronics"));

        verify(productService, times(1)).create(any(ProductDTO.class));
    }

    @Test
    void updateProduct_success() throws Exception {
        when(productService.update(any(Long.class), any(ProductDTO.class))).thenReturn(productDTO);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"));

        verify(productService, times(1)).update(any(Long.class), any(ProductDTO.class));
    }

    @Test
    void deleteProduct_success() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted successfully"));

        verify(productService, times(1)).delete(1L);
    }
}
