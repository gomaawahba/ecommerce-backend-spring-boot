package com.gomaa.service;

import com.gomaa.dto.*;
import com.gomaa.model.Customer;
import com.gomaa.repository.CustomerRepository;
import com.gomaa.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public void register(RegisterRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setJoined(LocalDate.now());
        customer.setStatus("ACTIVE");

        customerRepository.save(customer);
    }

    public AuthResponse login(LoginRequest request) {
        Customer customer = customerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(customer.getEmail());
        return new AuthResponse(token);
    }

    // ------------------ Forgot Password ------------------
    public void forgotPassword(ForgotPasswordRequest request) {
        Customer customer = customerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email not found"));

        // generate 6-digit code
        String token = String.valueOf((int)(Math.random() * 900000) + 100000);
        customer.setResetPasswordToken(token);
        customer.setResetTokenExpiry(LocalDate.now().plusDays(1)); // expires in 1 day
        customerRepository.save(customer);

        // send email
        String subject = "Password Reset Code";
        String body = "Your password reset code is: " + token;
        emailService.sendEmail(customer.getEmail(), subject, body);
    }

    // ------------------ Reset Password ------------------
    public void resetPassword(ResetPasswordRequest request) {
        Customer customer = customerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email not found"));

        if (customer.getResetPasswordToken() == null ||
                !customer.getResetPasswordToken().equals(request.getToken())) {
            throw new RuntimeException("Invalid reset token");
        }

        if (customer.getResetTokenExpiry() == null || customer.getResetTokenExpiry().isBefore(LocalDate.now())) {
            throw new RuntimeException("Token expired");
        }

        customer.setPassword(passwordEncoder.encode(request.getNewPassword()));
        customer.setResetPasswordToken(null);
        customer.setResetTokenExpiry(null);
        customerRepository.save(customer);
    }
}
