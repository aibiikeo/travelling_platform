package com.example.traveling_platform.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.SecureRandom;
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
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
    public String generateToken(String username) {
        return createToken(Map.of(), username);
    }



    public static String generateVerificationKey(int byteLength) {
        SecureRandom secureRandom = new SecureRandom();
        return new BigInteger(byteLength * 8, secureRandom).toString(16);
    }
    private String verificationKey = generateVerificationKey(64);

    public String generateVerificationToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                .signWith(SignatureAlgorithm.HS256, verificationKey)
                .compact();
    }

    public String getEmailFromToken(String token) {
//        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(verificationKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
//        } catch (ExpiredJwtException e) {
//            return e.getClaims().getSubject();
//        }
//        catch (Exception e) {
//            throw new IllegalArgumentException("Invalid token", e);
//        }
    }

    public String getEmailFromExpiredToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(verificationKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (ExpiredJwtException e) {

            return e.getClaims().getSubject();
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to parse token", e);
        }
    }

}
