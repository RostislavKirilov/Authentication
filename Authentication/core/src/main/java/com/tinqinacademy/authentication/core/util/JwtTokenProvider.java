package com.tinqinacademy.authentication.core.util;

import com.tinqinacademy.authentication.persistance.repositories.BlacklistedTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;


@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    private final BlacklistedTokenRepository blacklistedTokenRepository;

    public JwtTokenProvider ( BlacklistedTokenRepository blacklistedTokenRepository ) {
        this.blacklistedTokenRepository = blacklistedTokenRepository;
    }


    //BASE64 - декодира ключа от пропъртитата
    private SecretKey getSignInKey () {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes); // преобразува декодираното в ключ
    }

    public String generateToken ( String username, String userId, String role ) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 300000))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            if (blacklistedTokenRepository.existsById(token)) {
                return false;
            }

            Date expirationDate = claims.getExpiration();
            return expirationDate != null && expirationDate.after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }


    public Authentication getAuthentication ( String token ) {
        Claims claims = Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String username = claims.getSubject();
        return new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
    }

    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("role", String.class);
    }


    public Date getExpirationDateFromToken ( String token ) {
        Claims claims = Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getExpiration();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("username", String.class);
    }
}