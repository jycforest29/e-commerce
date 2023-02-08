package com.jycforest29.commerce.security.service;

import com.jycforest29.commerce.security.dto.register.AuthUserRequestDto;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

// DB에서 유저 정보를 가져오는 역할
@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private AuthUserRepository authUserRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthUser authUser = getAuthenticatedAuthUser(username);
        if (authUser == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new org.springframework.security.core.userdetails.User(authUser.getUsername(), authUser.getPassword(),
                new ArrayList<>());
    }

    public void register(AuthUserRequestDto authUserRequest){
        authUserRepository.save(AuthUser.from(authUserRequest));
    }

    public AuthUser getAuthenticatedAuthUser(String username){
        return authUserRepository.findByUsername(username);
    }
}
