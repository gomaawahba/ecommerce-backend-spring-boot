package com.gomaa.service;

import com.gomaa.dto.ReviewDTO;
import com.gomaa.model.Customer;
import com.gomaa.model.Product;
import com.gomaa.model.Review;
import com.gomaa.repository.CustomerRepository;
import com.gomaa.repository.ProductRepository;
import com.gomaa.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public ReviewDTO create(ReviewDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Review review = Review.builder()
                .comment(dto.getComment())
                .rating(dto.getRating())
                .customer(customer)
                .product(product)
                .build();

        reviewRepository.save(review);
        dto.setId(review.getId());
        return dto;
    }

    public List<ReviewDTO> getByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return reviewRepository.findByProduct(product)
                .stream()
                .map(r -> ReviewDTO.builder()
                        .id(r.getId())
                        .comment(r.getComment())
                        .rating(r.getRating())
                        .customerId(r.getCustomer().getId())
                        .productId(productId)
                        .createdAt(r.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        reviewRepository.deleteById(id);
    }
}
