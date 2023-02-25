package com.jycforest29.commerce.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
// WebSecurity와 HttpSecurity 모두에 커스텀 필터 적용할 수 있도록 config 설정
public class WebSecurityConfig implements WebMvcConfigurer {
    private final JwtTokenProvider jwtTokenProvider;

//    // 정적 자원에 대해서는 security를 적용하지 않음
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer(){
//        return web -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
//    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                // 세션 사용하지 않음
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable() // form Login 안함
                .httpBasic().disable() // jwt는 httpBearer 방식이므로 httpBasic 방식은 사용 안함
                // h2 콘솔 허용(나중에 인메모리 db 쓸 때를 위해)
                .headers()
                .frameOptions()
                .sameOrigin()
                .and()
//                .authorizeRequests().antMatchers("/authenticate/**").permitAll() // 회원가입, 로그인 허용
//                .and()
//                .authorizeRequests().antMatchers(HttpMethod.GET, "**/review/**").permitAll() // 리뷰 조회 허용
//                .and()
                .authorizeRequests().anyRequest().permitAll()
                .and()
                .addFilterBefore(new JwtRequestFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
