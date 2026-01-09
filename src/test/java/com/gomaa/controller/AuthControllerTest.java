package com.gomaa.controller;

import com.gomaa.dto.AuthResponse;
import com.gomaa.dto.LoginRequest;
import com.gomaa.dto.RegisterRequest;
import com.gomaa.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        // بيانات gomaa wahba
        registerRequest = new RegisterRequest();
        registerRequest.setName("gomaa wahba");
        registerRequest.setEmail("gomaa@example.com");
        registerRequest.setPhone("123456789");
        registerRequest.setPassword("123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("gomaa@example.com");
        loginRequest.setPassword("123");

        authResponse = new AuthResponse("jwtToken123");
    }

    @Test
    void register_success() throws Exception {
        doNothing().when(authService).register(registerRequest);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Registered successfully"));

        verify(authService, times(1)).register(registerRequest);
    }

    @Test
    void login_success() throws Exception {
        when(authService.login(loginRequest)).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwtToken123"));

        verify(authService, times(1)).login(loginRequest);
    }
}
