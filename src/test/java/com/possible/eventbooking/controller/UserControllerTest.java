package com.possible.eventbooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.possible.eventbooking.dto.LoginDto;
import com.possible.eventbooking.dto.ResponseDto;
import com.possible.eventbooking.dto.UserDto;
import com.possible.eventbooking.model.User;
import com.possible.eventbooking.model.UserRole;
import com.possible.eventbooking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setEmail("testuser@example.com");
        userDto.setPassword("password");
        userDto.setUserRole("USER");
    }

    @Test
    void testCreateUser() throws Exception {
        MvcResult result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andReturn();

        ResponseDto<?> response = objectMapper.readValue(result.getResponse().getContentAsString(), ResponseDto.class);
        assertThat(response.getStatusCode()).isEqualTo(201);
        assertThat(response.getResponseMessage()).isEqualTo("Account created successfully");

        User user = userRepository.findByEmail(userDto.getEmail()).orElse(null);
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo(userDto.getName());
    }

    @Test
    void testGetUsers() throws Exception {
        User user = new User();
        user.setName("Test User");
        user.setEmail("testuser@example.com");
        user.setPassword("password");
        user.setUserRoles(Collections.singletonList(UserRole.USER));
        userRepository.save(user);

        MvcResult result = mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ResponseDto<?> response = objectMapper.readValue(result.getResponse().getContentAsString(), ResponseDto.class);
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getResponseMessage()).isEqualTo("Successfully load User booked reservation");
        assertThat(response.getData()).isNotNull();
    }

    @Test
    void testAuthenticateUser() throws Exception {
        User user = new User();
        user.setName("Test User");
        user.setEmail("testuser@example.com");
        user.setPassword("$2a$10$xLNjMh0WrBx0JnoQ1vCrxeeWznAOJRQzdQJSQSwDCy25SXQBqftWG"); // bcrypt-encoded "password"
        user.setUserRoles(Collections.singletonList(UserRole.USER));
        user.isEnabled();
        userRepository.save(user);

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("testuser@example.com");
        loginDto.setPassword("test_12345");

        MvcResult result = mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andReturn();

        ResponseDto<?> response = objectMapper.readValue(result.getResponse().getContentAsString(), ResponseDto.class);
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getResponseMessage()).isEqualTo("User login successfully");
        assertThat(response.getData()).isNotNull();
    }
}
