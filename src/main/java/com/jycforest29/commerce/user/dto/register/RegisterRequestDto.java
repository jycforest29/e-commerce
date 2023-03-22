package com.jycforest29.commerce.user.dto.register;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RegisterRequestDto {
    @NotBlank
    @Pattern(regexp = "^(?=.+[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]{5,10}$",
            message = "아이디는 5~10자리 이내 영문, 숫자로 이루어져야 하고 숫자로 시작할 수 없습니다")
    private String username;
    @NotBlank
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[@#$%])(?=.*[0-9])[a-zA-Z0-9@#$%]{5,10}$",
            message = "비밀번호는 5~10자리 이내 영문, 숫자, (@, #, $, %)의 특수문자로 이루어져야 하고 특수문자로 시작할 수 없습니다")
    private String password;
    @NotBlank
    @Pattern(regexp = "^(?=.*[a-zA-Z])[a-zA-Z]{5,10}$", message = "닉네임은 5~10자리 이내 영문으로만 이루어져야 합니다")
    private String nickname;
}
