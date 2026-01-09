package com.gomaa.service;

import com.gomaa.dto.CustomerDTO;
import com.gomaa.model.Customer;
import com.gomaa.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("gomaa wahba");
        customer.setEmail("gomaa@example.com");
        customer.setPhone("123456789");
        customer.setJoined(LocalDate.now());
        customer.setStatus("ACTIVE");

        customerDTO = new CustomerDTO();
        customerDTO.setId(1L);
        customerDTO.setName("gomaa wahba");
        customerDTO.setEmail("gomaa@example.com");
        customerDTO.setPhone("123456789");
        customerDTO.setJoined(LocalDate.now());
        customerDTO.setStatus("ACTIVE");
    }

    @Test
    void createCustomer_success() {
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerDTO created = customerService.create(customerDTO);

        assertNotNull(created);
        assertEquals("gomaa wahba", created.getName());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void getAllCustomers_success() {
        when(customerRepository.findAll()).thenReturn(List.of(customer));

        List<CustomerDTO> customers = customerService.getAll();

        assertEquals(1, customers.size());
        assertEquals("gomaa wahba", customers.get(0).getName());
    }

    @Test
    void getById_success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerDTO dto = customerService.getById(1L);

        assertEquals("gomaa wahba", dto.getName());
    }

    @Test
    void updateCustomer_success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerDTO updated = customerService.update(1L, customerDTO);

        assertEquals("gomaa wahba", updated.getName());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void deleteCustomer_success() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        doNothing().when(customerRepository).deleteById(1L);

        assertDoesNotThrow(() -> customerService.delete(1L));
        verify(customerRepository, times(1)).deleteById(1L);
    }
}
