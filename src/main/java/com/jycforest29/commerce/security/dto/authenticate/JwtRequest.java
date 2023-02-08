package com.jycforest29.commerce.security.dto.authenticate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

//need default constructor for JSON Parsing
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JwtRequest implements Serializable {
    private static final long serialVersionUID = 5926468583005150707L; // 자바 직렬화 버전의 고유값
    @NotEmpty(message = "아이디 칸이 공란입니다.")
    private String username;
    @NotEmpty(message = "비밀번호 칸이 공란입니다.")
    private String password;
}
