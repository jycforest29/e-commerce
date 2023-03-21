package com.jycforest29.commerce.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jycforest29.commerce.testcontainers.DockerComposeTestContainer;
import com.jycforest29.commerce.user.dto.authenticate.LoginRequestDto;
import com.jycforest29.commerce.user.dto.authenticate.LoginResponseDto;
import com.jycforest29.commerce.user.dto.register.RegisterRequestDto;
import com.jycforest29.commerce.user.service.AuthUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class AuthUserControllerTest extends DockerComposeTestContainer{
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AuthUserService authUserService;
    RegisterRequestDto registerRequestDto = new RegisterRequestDto(
            "testuser1",
            "pw1234@",
            "testuser"
    );
    LoginRequestDto loginRequestDto = new LoginRequestDto(
            "testuser1",
            "pw1234@"
    );
    LoginResponseDto loginResponseDto = new LoginResponseDto(
            "accessToken",
            null
    );
    @Test
    void 회원가입을_수행한다() throws Exception {
        // given
        String dtoAsContent = objectMapper.writeValueAsString(registerRequestDto);
        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/authenticate/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoAsContent)
                        .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    void 로그인을_수행한다() throws Exception {
        // given
        String dtoAsContent = objectMapper.writeValueAsString(loginRequestDto);
        given(authUserService.login(loginRequestDto)).willReturn(loginResponseDto);
        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/authenticate/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoAsContent)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value(null));
    }
}