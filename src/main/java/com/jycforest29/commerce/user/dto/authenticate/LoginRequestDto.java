package com.jycforest29.commerce.user.dto.authenticate;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode // 테스트
@Getter
@AllArgsConstructor // 테스트
public class LoginRequestDto {
    @NotEmpty(message = "아이디 칸이 공란입니다.")
    private String username;
    @NotEmpty(message = "비밀번호 칸이 공란입니다.")
    private String password;
}
