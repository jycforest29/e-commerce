package com.jycforest29.commerce.user.service;

import com.jycforest29.commerce.user.dto.authenticate.LoginRequestDto;
import com.jycforest29.commerce.user.dto.authenticate.LoginResponseDto;
import com.jycforest29.commerce.user.dto.register.RegisterRequestDto;

public interface AuthUserService {
    void register(RegisterRequestDto registerRequestDto);

    LoginResponseDto login(LoginRequestDto loginRequestDto) throws Exception;
}
