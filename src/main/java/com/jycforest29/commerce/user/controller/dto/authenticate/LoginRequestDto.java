package com.jycforest29.commerce.user.controller.dto.authenticate;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LoginRequestDto {
    @NotEmpty(message = "아이디 칸이 공란입니다.")
    private String username;
    @NotEmpty(message = "비밀번호 칸이 공란입니다.")
    private String password;
}
