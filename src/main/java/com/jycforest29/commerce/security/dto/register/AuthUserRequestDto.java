package com.jycforest29.commerce.security.dto.register;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Size;


@Builder
@Getter
public class AuthUserRequestDto {
    @Size(min = 5, max = 10, message = "아이디는 5~10 글자여야 합니다.")
    private String username;
    @Size(min = 5, max = 10, message = "비밀번호는 5~10 글자여야 합니다.")
    private String password;
    @Size(min = 5, max = 10, message = "닉네임은 5~10 글자여야 합니다.")
    private String nickname;
}
