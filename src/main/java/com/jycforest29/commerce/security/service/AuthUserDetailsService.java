package com.jycforest29.commerce.security.service;

import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// UsernamePasswordAuthenticationFilter 클래스에서 인증을 위해 호출하는 메서드
@RequiredArgsConstructor
@Service
public class AuthUserDetailsService implements UserDetailsService {
    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthUser authUser = authUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("username에 대한 정보를 찾을 수 없습니다"));
        return User.builder()
                .username(username)
                .password(passwordEncoder.encode(authUser.getPassword()))
                .authorities(authUser.getAuthorities())
                .build();
    }
}
