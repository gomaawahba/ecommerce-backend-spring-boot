package com.gomaa.service;

import com.gomaa.dto.CartItemDTO;
import com.gomaa.dto.OrderDTO;
import com.gomaa.exception.ResourceNotFoundException;
import com.gomaa.model.CartItem;
import com.gomaa.model.Customer;
import com.gomaa.model.Order;
import com.gomaa.repository.CustomerRepository;
import com.gomaa.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CartItemService cartItemService;

    @InjectMocks
    private OrderService orderService;

    private Customer customer;
    private Order order;
    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("gomaa wahba");

        order = new Order();
        order.setId(1L);
        order.setOrderNumber("ORD-001");
        order.setCustomer(customer);
        order.setDate(LocalDate.now());
        order.setStatus("Processing");
        order.setPaymentStatus("Paid");
        order.setWayPayment("Paypal");

        orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setOrderNumber("ORD-001");
        orderDTO.setCustomerId(1L);
        orderDTO.setStatus("Processing");
        orderDTO.setPaymentStatus("Paid");
        orderDTO.setWayPayment("Paypal");
        orderDTO.setItems(List.of(new CartItemDTO(1L, 2, 50)));
    }

    @Test
    void createOrder_success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(cartItemService.saveAll(anyList(), anyLong())).thenReturn(List.of());

        OrderDTO saved = orderService.create(orderDTO);

        assertNotNull(saved);
        assertEquals("ORD-001", saved.getOrderNumber());

        verify(customerRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
        verify(cartItemService).saveAll(anyList(), eq(1L));
    }

    @Test
    void getById_success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderDTO dto = orderService.getById(1L);

        assertNotNull(dto);
        assertEquals("ORD-001", dto.getOrderNumber());
        verify(orderRepository).findById(1L);
    }

    @Test
    void getById_notFound_throwsException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> orderService.getById(1L));

        assertEquals("Order with id 1 not found", ex.getMessage());
    }

    @Test
    void deleteOrder_success() {
        when(orderRepository.existsById(1L)).thenReturn(true);

        orderService.delete(1L);

        verify(orderRepository).deleteById(1L);
    }

    @Test
    void deleteOrder_notFound_throwsException() {
        when(orderRepository.existsById(1L)).thenReturn(false);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> orderService.delete(1L));

        assertEquals("Order with id 1 not found", ex.getMessage());
    }

    @Test
    void exportCsv_success() throws Exception {
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);

        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(response.getWriter()).thenReturn(writer);

        orderService.exportCsv(response);

        verify(writer).println("Order Number,Customer,Date,Items Count,Total,Status,Payment Status,Way Payment");
        verify(writer).println(contains("ORD-001,gomaa wahba"));
    }
}
