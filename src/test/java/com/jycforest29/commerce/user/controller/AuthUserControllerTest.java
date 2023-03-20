//package com.jycforest29.commerce.user.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.jycforest29.commerce.common.aop.LoginAuthUserResolver;
//import com.jycforest29.commerce.user.dto.authenticate.LoginRequestDto;
//import com.jycforest29.commerce.user.dto.authenticate.LoginResponseDto;
//import com.jycforest29.commerce.user.dto.register.RegisterRequestDto;
//import com.jycforest29.commerce.user.service.AuthUserService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import static org.mockito.BDDMockito.given;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@Import(LoginAuthUserResolver.class)
//class AuthUserControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//    @MockBean
//    private AuthUserService authUserService;
//    @Autowired
//    private ObjectMapper objectMapper;
//    RegisterRequestDto registerRequestDto = new LoginRequestDto();
//    LoginRequestDto loginRequestDto = new LoginRequestDto();
//    LoginResponseDto loginResponseDto = new LoginResponseDto();
//    @Test
//    void 회원가입을_수행한다() throws Exception {
//        // given
//        String dtoAsContent = objectMapper.writeValueAsString(registerRequestDto);
//        // when, then
//        mockMvc.perform(MockMvcRequestBuilders.post("/authenticate/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(dtoAsContent)
//                        .with(csrf()))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$[0].title").value("title"))
//                .andExpect(jsonPath("$[0].contents").value("contents"));
//    }
//
//    @Test
//    void 로그인을_수행한다() throws Exception {
//        // given
//        String dtoAsContent = objectMapper.writeValueAsString(loginRequestDto);
//        given(authUserService.login(loginRequestDto)).willReturn(loginResponseDto);
//        // when, then
//        mockMvc.perform(MockMvcRequestBuilders.post("/authenticate/login", 1L)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(dtoAsContent)
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].title").value("title"))
//                .andExpect(jsonPath("$[0].contents").value("contents"));
//    }
//}