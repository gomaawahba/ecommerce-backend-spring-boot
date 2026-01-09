package com.gomaa.service;

import com.gomaa.dto.DiscountDTO;
import com.gomaa.enums.DiscountType;
import com.gomaa.exception.ResourceNotFoundException;
import com.gomaa.model.Discount;
import com.gomaa.repository.DiscountRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscountServiceTest {

    @Mock
    private DiscountRepository discountRepository;

    @InjectMocks
    private DiscountService discountService;

    private Discount discount;
    private DiscountDTO discountDTO;

    @BeforeEach
    void setUp() {
        discount = new Discount();
        discount.setId(1L);
        discount.setCode("SUMMER25");
        discount.setName("Summer Sale");
        discount.setType(DiscountType.PERCENTAGE);
        discount.setValue(25.0);
        discount.setUsageLimit(100);
        discount.setUsedCount(10);
        discount.setStartDate(LocalDate.now().minusDays(1));
        discount.setEndDate(LocalDate.now().plusDays(10));
        discount.setActive(true);

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
    void createDiscount_success() {
        when(discountRepository.save(any(Discount.class))).thenReturn(discount);

        DiscountDTO saved = discountService.create(discountDTO);

        assertNotNull(saved);
        assertEquals("SUMMER25", saved.getCode());
        assertEquals("Summer Sale", saved.getName());
    }

    @Test
    void getById_success() {
        when(discountRepository.findById(1L)).thenReturn(Optional.of(discount));

        DiscountDTO dto = discountService.getById(1L);

        assertNotNull(dto);
        assertEquals("SUMMER25", dto.getCode());
    }

    @Test
    void getById_notFound_throwsException() {
        when(discountRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> discountService.getById(1L));

        assertEquals("Discount with id 1 not found", ex.getMessage());
    }

    @Test
    void updateDiscount_success() {
        when(discountRepository.findById(1L)).thenReturn(Optional.of(discount));
        when(discountRepository.save(any(Discount.class))).thenReturn(discount);

        discountDTO.setName("Updated Sale");
        DiscountDTO updated = discountService.update(1L, discountDTO);

        assertEquals("Updated Sale", updated.getName());
    }

    @Test
    void deleteDiscount_success() {
        when(discountRepository.existsById(1L)).thenReturn(true);

        discountService.delete(1L);

        verify(discountRepository).deleteById(1L);
    }

    @Test
    void deleteDiscount_notFound_throwsException() {
        when(discountRepository.existsById(1L)).thenReturn(false);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> discountService.delete(1L));

        assertEquals("Discount with id 1 not found", ex.getMessage());
    }

    @Test
    void exportCsv_success() throws Exception {
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);

        when(discountRepository.findAll()).thenReturn(List.of(discount));
        when(response.getWriter()).thenReturn(writer);

        discountService.exportCsv(response);

        verify(writer).println(contains("Code,Name,Type"));
        verify(writer).println(contains("SUMMER25,Summer Sale"));
    }
}
