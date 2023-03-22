package com.jycforest29.commerce.security.config;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
@Component
// creation이나 validation에 대한 Jwt 연산 수행함
public class JwtTokenProvider {
    public static long ACCESS_TOKEN_EXISTS = 30 * 60 * 1000; // 접근 토큰의 유효기간 30분. 30분 지나면 자동 로그아웃 됨
    @Value("\\${jwt.secret}")
    private String secret;

//    @PostConstruct
//    protected void init() {
//        secret = Base64.getEncoder().encodeToString(secret.getBytes());
//    }

    //jwt token에서 정보를 검색하려면 비밀키가 필요함
    //Claim이란? 사용자에 대한 프로퍼티나 속성을 의미. jwt 이 Claim을 JSON을 이용해 정의함.

    // Authentication 객체는
        // principal : 사용자 id 혹은 User 객체
        // credentials : 비밀번호
        // authorities : 인증된 사용자의 권한 목록
        // details : 인증 부가 정보
        // Authenticated : 인증 여부
    public String generateAccessToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("username", userDetails.getUsername());
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXISTS))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        return "refreshToken"; // 일단 구현 못함
    }

    public Boolean validateToken(String token) {
        try{
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (ExpiredJwtException e){ // 만료
            e.printStackTrace();
            return false;
        }catch (JwtException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
        // UsernamePasswordAuthenticationToken(principal, credentials, authorities)
        // 현재 authorities는 모두 빈 리스트로 설정함 -> 변경 필요
        return new UsernamePasswordAuthenticationToken(claims.getSubject(), token, new ArrayList<>());
    }
}
