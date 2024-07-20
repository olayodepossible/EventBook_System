package com.possible.eventbooking.controller;

import com.possible.eventbooking.dto.LoginDto;
import com.possible.eventbooking.dto.ResponseDto;
import com.possible.eventbooking.dto.UserDto;
import com.possible.eventbooking.model.User;
import com.possible.eventbooking.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping()
@Tag(name = "users")
@RequiredArgsConstructor
@Slf4j
public class UserController {


    private final UserService userService;
    @PostMapping("/users")
    public ResponseEntity<ResponseDto<Object>> createUser(@Valid @RequestBody UserDto userDTO) {
        return new ResponseEntity<>(userService.createUser(userDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Get All Users", description = "This endpoint returns all the User")
    @GetMapping("/users")
    public ResponseEntity<ResponseDto<Object>> getUsers() {
        List<User> users = userService.getAllUser();
        ResponseDto<Object> resp = ResponseDto.builder()
                .statusCode(200)
                .responseMessage(users.isEmpty() ? "No reservation found" : "Successfully load User booked reservation")
                .data(users)
                .build();
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @PostMapping("/auth")
    public ResponseEntity<ResponseDto<Object>> authenticateUser(@RequestBody LoginDto credentials) {
        return ResponseEntity.ok().body(userService.authenticateUser(credentials));
    }
}

