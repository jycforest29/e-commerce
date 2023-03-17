package com.jycforest29.commerce.user.service;

import com.jycforest29.commerce.common.exception.CustomException;
import com.jycforest29.commerce.common.exception.ExceptionCode;
import com.jycforest29.commerce.security.config.JwtTokenProvider;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import com.jycforest29.commerce.user.dto.authenticate.LoginRequestDto;
import com.jycforest29.commerce.user.dto.authenticate.LoginResponseDto;
import com.jycforest29.commerce.user.dto.register.RegisterRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@RequiredArgsConstructor
@Service
public class AuthUserService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    @Transactional
    public void register(RegisterRequestDto registerRequestDto){
        if (authUserRepository.existsByUsername(registerRequestDto.getUsername())){
            throw new CustomException(ExceptionCode.USERNAME_DUPLICATED);
        }
        String encodedPassword = passwordEncoder.encode(registerRequestDto.getPassword());
        authUserRepository.save(
                AuthUser.builder()
                        .username(registerRequestDto.getUsername())
                        .password(encodedPassword)
                        .nickname(registerRequestDto.getNickname())
                        .build()
        );
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) throws Exception {
        Authentication authentication = authenticate(loginRequestDto.getUsername(), loginRequestDto.getPassword());
        // Authentication에 있는 인증된 Principal 객체를 UserDetails 객체로 꺼냄
        // UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return LoginResponseDto.builder()
                .accessToken(jwtTokenProvider.generateAccessToken(authentication))
                .refreshToken(jwtTokenProvider.generateRefreshToken(authentication))
                .build();
    }

    private Authentication authenticate(String username, String password) throws Exception {
        try {
            // DI 받은 authenticationManager로 로그인 시도 -> UserDetailsService 상속받은 클래스
            // 내부에서 loadByUsername() 실행 -> authentication으로 토큰이 유효한지 검증
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            log.info(e.getMessage());
            throw new Exception("INVALID_CREDENTIALS", e);
        } catch (Exception e){
            log.info(e.getMessage());
            throw new Exception();
        }
    }
}
