package com.gomaa.service;

import com.gomaa.dto.AuthResponse;
import com.gomaa.dto.LoginRequest;
import com.gomaa.dto.RegisterRequest;
import com.gomaa.model.Customer;
import com.gomaa.repository.CustomerRepository;
import com.gomaa.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private Customer customer;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("gomaa wahba");
        registerRequest.setEmail("gomaa@example.com");
        registerRequest.setPhone("123456789");
        registerRequest.setPassword("123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("gomaa@example.com");
        loginRequest.setPassword("123");

        customer = new Customer();
        customer.setId(1L);
        customer.setName("gomaa wahba");
        customer.setEmail("gomaa@example.com");
        customer.setPassword("encodedPassword");
        customer.setJoined(LocalDate.now());
        customer.setStatus("ACTIVE");
    }

    @Test
    void register_success() {
        when(customerRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        assertDoesNotThrow(() -> authService.register(registerRequest));

        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void register_emailAlreadyExists_throwsException() {
        when(customerRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(registerRequest));
        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void login_success() {
        when(customerRepository.findByEmail("gomaa@example.com")).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches("123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("gomaa@example.com")).thenReturn("jwtToken");

        AuthResponse response = authService.login(loginRequest);
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
    }

    @Test
    void login_invalidEmail_throwsException() {
        when(customerRepository.findByEmail("gomaa@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(loginRequest));
        assertEquals("Invalid email or password", exception.getMessage());
    }

    @Test
    void login_invalidPassword_throwsException() {
        when(customerRepository.findByEmail("gomaa@example.com")).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches("123", "encodedPassword")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(loginRequest));
        assertEquals("Invalid email or password", exception.getMessage());
    }
}
