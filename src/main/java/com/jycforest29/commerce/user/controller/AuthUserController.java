package com.jycforest29.commerce.user.controller;

import com.jycforest29.commerce.user.controller.dto.authenticate.LoginRequestDto;
import com.jycforest29.commerce.user.controller.dto.authenticate.LoginResponseDto;
import com.jycforest29.commerce.user.controller.dto.register.RegisterRequestDto;
import com.jycforest29.commerce.user.service.AuthUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping(value = "/authenticate")
@RequiredArgsConstructor
@RestController
public class AuthUserController {
    private final AuthUserService authUserService;

    @PostMapping(value = "/register")
    public ResponseEntity<Object> registerAuthUser(@Valid @RequestBody RegisterRequestDto registerRequestDto)
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
