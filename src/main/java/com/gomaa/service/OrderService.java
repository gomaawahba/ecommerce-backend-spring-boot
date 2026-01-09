package com.gomaa.service;

import com.gomaa.dto.CartItemDTO;
import com.gomaa.dto.OrderDTO;
import com.gomaa.exception.ResourceNotFoundException;
import com.gomaa.model.CartItem;
import com.gomaa.model.Customer;
import com.gomaa.model.Order;
import com.gomaa.repository.CustomerRepository;
import com.gomaa.repository.OrderRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final CartItemService cartItemService;

    // ------------------- DTO Conversion -------------------

    private Order toEntity(OrderDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer with id " + dto.getCustomerId() + " not found"));

        Order order = new Order();
        order.setId(dto.getId());
        order.setOrderNumber(dto.getOrderNumber());
        order.setCustomer(customer);
        order.setDate(dto.getDate() != null ? dto.getDate() : LocalDate.now());
        order.setStatus(dto.getStatus());
        order.setPaymentStatus(dto.getPaymentStatus());
        order.setWayPayment(dto.getWayPayment());

        return order;
    }

    private OrderDTO toDto(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setCustomerId(order.getCustomer().getId());
        dto.setDate(order.getDate());
        dto.setStatus(order.getStatus());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setWayPayment(order.getWayPayment());

        // تجنب LazyInitializationException
        List<CartItemDTO> items = order.getItems() != null
                ? order.getItems().stream().map(this::toItemDto).toList()
                : List.of();

        dto.setItems(items);

        double total = items.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();

        dto.setTotal(total);

        return dto;
    }

    private CartItemDTO toItemDto(CartItem item) {
        CartItemDTO dto = new CartItemDTO();
        dto.setProductId(item.getProduct().getId());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        return dto;
    }

    // ------------------- CRUD -------------------

    @Transactional
    public OrderDTO create(OrderDTO dto) {
        Order order = toEntity(dto);
        Order savedOrder = orderRepository.save(order);

        List<CartItem> items = cartItemService.saveAll(dto.getItems(), savedOrder.getId());
        savedOrder.setItems(items);

        return toDto(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getAll() {
        return orderRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderDTO getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order with id " + id + " not found"));
        return toDto(order);
    }

    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order with id " + id + " not found");
        }
        orderRepository.deleteById(id);
    }

    // ------------------- Export CSV -------------------

    @Transactional(readOnly = true)
    public void exportCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=orders.csv");

        try (PrintWriter writer = response.getWriter()) {

            writer.println("Order Number,Customer,Date,Items Count,Total,Status,Payment Status,Way Payment");

            List<Order> orders = orderRepository.findAll();
            for (Order o : orders) {

                int itemsCount = o.getItems() != null ? o.getItems().size() : 0;
                double total = o.getItems() != null
                        ? o.getItems().stream()
                        .mapToDouble(i -> i.getPrice() * i.getQuantity())
                        .sum()
                        : 0;

                writer.println(
                        o.getOrderNumber() + "," +
                                o.getCustomer().getName() + "," +
                                o.getDate() + "," +
                                itemsCount + "," +
                                total + "," +
                                o.getStatus() + "," +
                                o.getPaymentStatus() + "," +
                                o.getWayPayment()
                );
            }
        }
    }
}
