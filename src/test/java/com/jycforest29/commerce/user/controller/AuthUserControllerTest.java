package com.jycforest29.commerce.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jycforest29.commerce.testcontainers.DockerComposeTestContainer;
import com.jycforest29.commerce.user.dto.authenticate.LoginRequestDto;
import com.jycforest29.commerce.user.dto.authenticate.LoginResponseDto;
import com.jycforest29.commerce.user.dto.register.RegisterRequestDto;
import com.jycforest29.commerce.user.service.AuthUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
// 왜 @WebMvcTest 로 수행할 경우 Cart 관련 DI 주입이 필요한건지 모르겠음
class AuthUserControllerTest extends DockerComposeTestContainer{
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthUserService authUserService;
    @Autowired
    private ObjectMapper objectMapper;
    @Test
    void 회원가입을_수행한다() throws Exception {
        // given
        RegisterRequestDto registerRequestDto = new RegisterRequestDto(
                "testuser1",
                "pw1234@",
                "testuser"
        );
        // when, then
        String dtoAsContent = objectMapper.writeValueAsString(registerRequestDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/authenticate/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoAsContent)
                        .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    void 회원가입시_비밀번호_validation에_실패한다() throws Exception {
        // given
        RegisterRequestDto registerRequestDto = new RegisterRequestDto(
                "testuser1",
                "1234", // 숫자로만 입력
                "testuser"
        );
        // when, then
        String dtoAsContent = objectMapper.writeValueAsString(registerRequestDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/authenticate/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoAsContent)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 로그인을_수행한다() throws Exception {
        // given
        LoginRequestDto loginRequestDto = new LoginRequestDto(
                "testuser1",
                "pw1234@"
        );
        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();

        given(authUserService.login(loginRequestDto)).willReturn(loginResponseDto);
        // when, then
        String dtoAsContent = objectMapper.writeValueAsString(loginRequestDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/authenticate/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoAsContent)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));
    }

    @Test
    void 로그인시_비밀번호_validation에_실패한다() throws Exception {
        // given
        LoginRequestDto loginRequestDto = new LoginRequestDto(
                "testuser1",
                ""
        );
        // when, then
        String dtoAsContent = objectMapper.writeValueAsString(loginRequestDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/authenticate/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoAsContent)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }
}