package com.jycforest29.commerce.user.controller;

import com.jycforest29.commerce.security.config.JwtTokenProvider;
import com.jycforest29.commerce.user.dto.authenticate.LoginRequestDto;
import com.jycforest29.commerce.user.dto.authenticate.LoginResponseDto;
import com.jycforest29.commerce.user.dto.register.RegisterRequestDto;
import com.jycforest29.commerce.user.service.AuthUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping(value = "/authenticate")
@CrossOrigin
@RestController
public class AuthUserController {
    private final AuthUserService authUserService;

    @PostMapping(value = "/register")
    public ResponseEntity<Object> RegisterAuthUser(@Valid @RequestBody RegisterRequestDto registerRequestDto)
            throws Exception {
        authUserService.register(registerRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping(value = "/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto)
            throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(authUserService.login(loginRequestDto));
    }
}
