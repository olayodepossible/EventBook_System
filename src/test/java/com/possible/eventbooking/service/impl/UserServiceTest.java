package com.possible.eventbooking.service.impl;

import com.possible.eventbooking.dto.LoginDto;
import com.possible.eventbooking.dto.ResponseDto;
import com.possible.eventbooking.dto.UserDto;
import com.possible.eventbooking.dto.UserInfo;
import com.possible.eventbooking.model.User;
import com.possible.eventbooking.model.UserRole;
import com.possible.eventbooking.repository.UserRepository;
import com.possible.eventbooking.security.JwtAuthenticationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtAuthenticationHelper jwtUtil;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto userDto;
    private User user;
    private LoginDto loginDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setEmail("testuser@example.com");
        userDto.setPassword("password");
        userDto.setUserRole("USER");

        user = new User();
        user.setName("Test User");
        user.setEmail("testuser@example.com");
        user.setPassword("encodedPassword");
        user.setUserRoles(Collections.singletonList(UserRole.USER));

        loginDto = new LoginDto();
        loginDto.setEmail("testuser@example.com");
        loginDto.setPassword("password");
    }

    @Test
    void testCreateUser_Success() {
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        ResponseDto<Object> response = userService.createUser(userDto);

        assertEquals(201, response.getStatusCode());
        assertEquals("Account created successfully", response.getResponseMessage());
        assertNotNull(response.getData());

        verify(userRepository, times(1)).findByEmail(userDto.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_Duplicate() {
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(user));

        ResponseDto<Object> response = userService.createUser(userDto);

        assertEquals(400, response.getStatusCode());
        assertEquals("Attempt to create duplicate user record", response.getResponseMessage());
        assertEquals(Collections.emptyList(), response.getData());

        verify(userRepository, times(1)).findByEmail(userDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAuthenticateUser_Success() {
        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDto.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(user.getUsername())).thenReturn("token");

        ResponseDto<Object> response = userService.authenticateUser(loginDto);

        assertEquals(200, response.getStatusCode());
        assertEquals("User login successfully", response.getResponseMessage());
        assertNotNull(response.getData());
        assertTrue(response.getData() instanceof UserInfo);

        verify(userRepository, times(1)).findByEmail(loginDto.getEmail());
    }

    @Test
    void testAuthenticateUser_InvalidCredentials() {
        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDto.getPassword(), user.getPassword())).thenReturn(false);

        ResponseDto<Object> response = userService.authenticateUser(loginDto);

        assertEquals(400, response.getStatusCode());
        assertEquals("Invalid user credential", response.getResponseMessage());

        verify(userRepository, times(1)).findByEmail(loginDto.getEmail());
    }

    @Test
    void testAuthenticateUser_InvalidCredential() {
        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));

        ResponseDto<Object> response = userService.authenticateUser(loginDto);

        assertEquals(400, response.getStatusCode());
        assertEquals("Invalid user credential", response.getResponseMessage());

        verify(userRepository, times(1)).findByEmail(loginDto.getEmail());
    }

    @Test
    void testLoadUserByUsername_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        User loadedUser = userService.loadUserByUsername(user.getEmail());

        assertEquals(user, loadedUser);
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    void testLoadUserByUsername_NotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(user.getEmail()));

        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }
}
