package com.jycforest29.commerce.security.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
// creation이나 validation에 대한 Jwt 연산 수행함
public class JwtTokenUtil implements Serializable {
    private static final long serialVersionUID = -2550185165626007488L;

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

    @Value("${jwt.secret}")
    private String secret;

    //validation
    //jwt token으로부터 username 가져오기
    public String getUsernameFromToken(String token) {
        // ::은 람다식에서 파라미터를 중복해서 사용하고 싶지 않을때 사용. 클래스 이름 :: 메서드 이름 혹은 참조변수 이름 :: 메서드 이름
        return getClaimFromToken(token, Claims::getSubject);
    }

    //validation
    //jwt token으로부터 expirationDate 가져오기
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    //validation
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) { // Function<T, R> 은 인자로 T룰 받고 R 리턴
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    //validation
    //jwt token에서 정보를 검색하려면 비밀키가 필요함
    //Claim이란? 사용자에 대한 프로퍼티나 속성을 의미. jwt 이 Claim을 JSON을 이용해 정의함.
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    //validation
    //jwt 토큰의 만료여부 확인
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    //creation
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>(); // 각 Username, ExpirationDate
        return doGenerateToken(claims, userDetails.getUsername());
    }

    //creation
    private String doGenerateToken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact(); //   compaction of the JWT to a URL-safe string
    }

    //validate
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);

        // check Claim
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
