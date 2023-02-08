//package com.jycforest29.commerce.security.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.jycforest29.commerce.security.config.JwtTokenUtil;
//import com.jycforest29.commerce.security.dto.register.AuthUserRequestDto;
//import com.jycforest29.commerce.security.service.JwtUserDetailsService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.test.web.servlet.MockMvc;
//
//@WebMvcTest(JwtAuthenticationControllerTest.class)
//class JwtAuthenticationControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private ObjectMapper objectMapper;
//    @MockBean
//    private AuthenticationManager authenticationManager;
//    @MockBean
//    private JwtTokenUtil jwtTokenUtil;
//    @MockBean
//    private JwtUserDetailsService jwtUserDetailsService;
//    AuthUserRequestDto authUserRequestDto
//
//    @BeforeEach
//    void init(){
//
//    }
//
//    @Test
//    void 새로운_유저_register(){
//        AuthUserRequestDto authUserRequestDto = AuthUserRequestDto.builder()
//                .username("test_username")
//                .password("test_password")
//                .nickname("test_nickname")
//                .build();
//
//    }
//
//    @Test
//    void 기존_유저_register(){
//
//    }
//
//    @Test
//    void 로그인(){
//
//    }
//
//    @Test
//    void 로그인_실패(){
//
//    }
//}