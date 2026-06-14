package com.saori.npo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.saori.npo.controller.request.LoginRequest;
import com.saori.npo.controller.response.LoginResponse;
import com.saori.npo.entity.User;
import com.saori.npo.mapper.UserMapper;
import com.saori.npo.security.JwtService;

import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class AuthController {

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public AuthController(
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @GetMapping("/api/auth/me")
    public String me(Principal principal) {
        return principal.getName();
    }

    @PostMapping("/api/auth/login")
    public LoginResponse login(
            @RequestBody LoginRequest request) {

        User user = userMapper.findByUsername(request.getUsername());

        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid username or password");
        }

        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "User is disabled");
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid username or password");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(
                token,
                user.getUsername(),
                user.getRole());
    }

}