package com.jycforest29.commerce.security.config;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 필터는 외부 요청을 가장 먼저 검증하는 곳
@RequiredArgsConstructor
@Component
// valid jwt를 확인해 authentication 수행하도록 하는 필터
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");

        // JWT Token은 Bearer token임.
            // 토큰을 소유한 사람에게 권한을 부여하는 일반적인 토큰 클래스.(jwt, oauth 등에서 사용)
            // https 같이 암호화된 프로토콜로 제공되는 보안이 필요함.
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            try {
                String accessToken = requestTokenHeader.substring(7);
                if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                    // 시큐리티 인메모리 db는 SecurityContextHolder.
                    // 여기는 Authentication 객체만 들어갈 수 있음.
                    // Authentication 안에는 User 정보가 있어야 하고 이는 UserDetails를 상속받아야 함.
                    // 따라서 UserDetails를 상속받는 User를 만들어야 함.
                    SecurityContextHolder.getContext().setAuthentication(authentication); // 유저 인증됨
                }
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("헤더에 access token이 없음");
            } catch (ExpiredJwtException e) {
                throw new RuntimeException("access token이 만료됨");
            }
        } else {
            logger.warn("Bearer 형식이 아님");
        }
        filterChain.doFilter(request, response);
    }
}
