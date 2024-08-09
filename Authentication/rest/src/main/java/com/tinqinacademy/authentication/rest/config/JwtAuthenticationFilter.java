package com.tinqinacademy.authentication.rest.config;

import com.tinqinacademy.authentication.core.util.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtTokenUtil jwtTokenUtil;
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public JwtAuthenticationFilter ( JwtTokenUtil jwtTokenUtil ) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public void doFilter ( ServletRequest request, ServletResponse response, FilterChain chain )
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = httpRequest.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenUtil.validateToken(token)) {
                SecurityContextHolder.getContext().setAuthentication(jwtTokenUtil.getAuthentication(token));
            }
        }

        chain.doFilter(request, response);
    }
}