package com.possible.eventbooking.service.impl;

import com.possible.eventbooking.dto.LoginDto;
import com.possible.eventbooking.dto.ResponseDto;
import com.possible.eventbooking.dto.UserDto;
import com.possible.eventbooking.dto.UserInfo;
import com.possible.eventbooking.model.User;
import com.possible.eventbooking.model.UserRole;
import com.possible.eventbooking.repository.UserRepository;
import com.possible.eventbooking.security.JwtAuthenticationHelper;
import com.possible.eventbooking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationHelper jwtUtil;
    private static final String SUCCESSFUL_LOGIN = "User login successfully";


    public ResponseDto<Object> createUser(UserDto userDto) {
        Optional<User> optionalUser = userRepository.findByEmail(userDto.getEmail());
        if (optionalUser.isPresent()){
            return ResponseDto.builder()
                    .statusCode(400)
                    .responseMessage("Attempt to create duplicate user record")
                    .data(Collections.emptyList())
                    .build();
        }
        List<UserRole> userRoles = new ArrayList<>(List.of(UserRole.USER));
        if (!userDto.getUserRole().isEmpty()){
            userRoles.add(UserRole.valueOf(userDto.getUserRole()));
        }

        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setUserRoles(userRoles);
        User createdUser = userRepository.save(user);
        return ResponseDto.builder()
                .statusCode(201)
                .responseMessage("Account created successfully")
                .data(createdUser)
                .build();
    }

    @Override
    public ResponseDto<Object> authenticateUser(LoginDto loginDto) {
        User appUser = loadUserByUsername(loginDto.getEmail());

        if (!appUser.isEnabled()){
            return ResponseDto.builder()
                    .statusCode(400)
                    .responseMessage("User Account deactivated")
                    .build();
        }
        if (!passwordEncoder.matches(loginDto.getPassword(), appUser.getPassword())){
            return ResponseDto.builder()
                    .statusCode(400)
                    .responseMessage("Invalid user credential")
                    .build();
        }
        String token = jwtUtil.generateToken(appUser.getUsername());

        UserInfo userInfo = UserInfo.builder()
                .name(appUser.getName())
                .username(appUser.getUsername())
                .token(token)
                .build();

        return ResponseDto.builder()
                .statusCode(200)
                .responseMessage(SUCCESSFUL_LOGIN)
                .data(userInfo)
                .build();
    }

    @Override
    public List<User> getAllUser() {
         return userRepository.findAll();
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow( () -> new UsernameNotFoundException("Username: " + username + " not found"));
    }
}

