package com.gomaa.service;

import com.gomaa.dto.CustomerDTO;
import com.gomaa.exception.ResourceNotFoundException;
import com.gomaa.model.Customer;
import com.gomaa.repository.CustomerRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    // ------------------- DTO Conversion -------------------

    private Customer toEntity(CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setId(dto.getId());
        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setJoined(dto.getJoined());
        customer.setStatus(dto.getStatus());
        return customer;
    }

    private CustomerDTO toDto(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setJoined(customer.getJoined());
        dto.setStatus(customer.getStatus());

        // تجنب LazyInitializationException
        int ordersCount = customer.getOrders() != null ? customer.getOrders().size() : 0;
        double totalSpent = customer.getOrders() != null
                ? customer.getOrders().stream().mapToDouble(o -> o.getTotal()).sum()
                : 0;

        dto.setOrdersCount(ordersCount);
        dto.setTotalSpent(totalSpent);

        return dto;
    }

    // ------------------- CRUD -------------------

    public CustomerDTO create(CustomerDTO dto) {
        Customer saved = customerRepository.save(toEntity(dto));
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<CustomerDTO> getAll() {
        return customerRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CustomerDTO getById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id " + id + " not found"));
        return toDto(customer);
    }

    public CustomerDTO update(Long id, CustomerDTO dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setJoined(dto.getJoined());
        customer.setStatus(dto.getStatus());

        Customer updated = customerRepository.save(customer);
        return toDto(updated);
    }

    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found");
        }
        customerRepository.deleteById(id);
    }

    // ------------------- Export CSV -------------------

    @Transactional(readOnly = true)
    public void exportCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=customers.csv");

        try (PrintWriter writer = response.getWriter()) {
            // Header
            writer.println("ID,Name,Email,Phone,Joined,Orders Count,Total Spent,Status");

            List<Customer> customers = customerRepository.findAll();
            for (Customer c : customers) {
                int ordersCount = c.getOrders() != null ? c.getOrders().size() : 0;
                double totalSpent = c.getOrders() != null
                        ? c.getOrders().stream().mapToDouble(o -> o.getTotal()).sum()
                        : 0;

                writer.println(
                        c.getId() + "," +
                                c.getName() + "," +
                                c.getEmail() + "," +
                                c.getPhone() + "," +
                                c.getJoined() + "," +
                                ordersCount + "," +
                                totalSpent + "," +
                                c.getStatus()
                );
            }
        }
    }
}
