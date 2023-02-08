package com.jycforest29.commerce.security.controller;

import com.jycforest29.commerce.security.config.JwtTokenUtil;
import com.jycforest29.commerce.security.dto.authenticate.JwtResponse;
import com.jycforest29.commerce.security.dto.authenticate.JwtRequest;
import com.jycforest29.commerce.security.dto.register.AuthUserRequestDto;
import com.jycforest29.commerce.security.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping(value = "/authenticate")
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @PostMapping
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody JwtRequest jwtRequest) throws Exception {
        authenticate(jwtRequest.getUsername(), jwtRequest.getPassword());
        final UserDetails userDetails = jwtUserDetailsService
                .loadUserByUsername(jwtRequest.getUsername());
        // generateToken -> doGenerateToken 을 통해 jwtTokenUtil의 creation 과정 수행
        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }


    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> saveAuthUser(@Valid @RequestBody AuthUserRequestDto authUserRequest) throws Exception {
        jwtUserDetailsService.register(authUserRequest);
        return ResponseEntity.ok().build();
    }


    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
