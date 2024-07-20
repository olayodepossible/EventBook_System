package com.possible.eventbooking.service;

import com.possible.eventbooking.dto.LoginDto;
import com.possible.eventbooking.dto.ResponseDto;
import com.possible.eventbooking.dto.UserDto;
import com.possible.eventbooking.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
   ResponseDto<Object>  createUser(UserDto userDTO);
   ResponseDto<Object>  authenticateUser(LoginDto credentials);

    List<User> getAllUser();
}
