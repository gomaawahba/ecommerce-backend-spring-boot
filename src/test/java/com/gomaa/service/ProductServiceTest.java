package com.gomaa.service;

import com.gomaa.dto.ProductDTO;
import com.gomaa.model.Category;
import com.gomaa.model.Product;
import com.gomaa.repository.CategoryRepository;
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
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private Category category;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("Electronics")
                .build();

        product = Product.builder()
                .id(1L)
                .name("Laptop")
                .description("Gaming Laptop")
                .price(1500)
                .stock(10)
                .imageUrl("img.png")
                .category(category)
                .build();

        productDTO = ProductDTO.builder()
                .id(1L)
                .name("Laptop")
                .description("Gaming Laptop")
                .price(1500)
                .stock(10)
                .imageUrl("img.png")
                .categoryId(1L)
                .build();
    }

    @Test
    void createProduct_success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDTO result = productService.create(productDTO);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        assertEquals("Electronics", result.getCategoryName());

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void getProductById_success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDTO result = productService.getById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Laptop", result.getName());
    }

    @Test
    void getAllProducts_success() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductDTO> result = productService.getAll();

        assertEquals(1, result.size());
    }

    @Test
    void searchByName_success() {
        when(productRepository.findByNameContainingIgnoreCase("lap"))
                .thenReturn(List.of(product));

        List<ProductDTO> result = productService.searchByName("lap");

        assertFalse(result.isEmpty());
    }

    @Test
    void deleteProduct_success() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.delete(1L);

        verify(productRepository).deleteById(1L);
    }





}
