package com.example.traveling_platform.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenUtil {

    @Value("357638792F423F4428472B4B6250655368566D597133743677397A2443264629")
    private String secretKey;

    @Value("8000")
    private long expirationTime;
    public String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // Use expirationTime here
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
    public String generateToken(String username) {
        return createToken(Map.of(), username);
    }
}
