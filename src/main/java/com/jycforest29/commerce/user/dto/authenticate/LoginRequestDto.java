package com.jycforest29.commerce.user.dto.authenticate;

import lombok.Getter;

import javax.validation.constraints.NotEmpty;

@Getter
public class LoginRequestDto {
    @NotEmpty(message = "아이디 칸이 공란입니다.")
    private String username;
    @NotEmpty(message = "비밀번호 칸이 공란입니다.")
    private String password;
}
