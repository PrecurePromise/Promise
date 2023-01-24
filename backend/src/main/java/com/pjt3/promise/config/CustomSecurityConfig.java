package com.pjt3.promise.config;

import com.pjt3.promise.common.auth.JwtAuthenticationFilter;
import com.pjt3.promise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@RequiredArgsConstructor
public class CustomSecurityConfig extends AbstractHttpConfigurer<CustomSecurityConfig, HttpSecurity> {

    private final UserRepository userRepository;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        http.addFilter(new JwtAuthenticationFilter(authenticationManager, userRepository));
    }
}
