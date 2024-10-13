package org.example.gateway.controller;

import org.example.gateway.dto.UserValidationRequest;
import org.example.gateway.feign.UserServiceClient;
import org.example.gateway.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserServiceClient userServiceClient;

    @Autowired
    public AuthController(JwtTokenProvider jwtTokenProvider, UserServiceClient userServiceClient) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userServiceClient = userServiceClient;
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@RequestBody UserValidationRequest userValidationRequest) {
        Boolean isValidUser = userServiceClient.validateUser(userValidationRequest);

        if (Boolean.TRUE.equals(isValidUser)) {
            String token = jwtTokenProvider.generateToken(userValidationRequest.getUsername(), userValidationRequest.getPassword(), userValidationRequest.getRoles());
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/reg")
    public ResponseEntity<String> registerUser(@RequestBody UserValidationRequest userValidationRequest) {
        userServiceClient.regUser(userValidationRequest);
        return ResponseEntity.ok("Register successful");
    }
}
