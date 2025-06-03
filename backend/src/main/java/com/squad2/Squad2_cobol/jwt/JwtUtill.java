package com.squad2.Squad2_cobol.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
public class JwtUtill {

    public static final String JWT_BEARER = "Bearer ";
    public static final String JWT_AUTHORIZATION = "Authorization";
    private static final String SECRET_KEY_BASE64 = "AxzqjNqzr6Z+8L/HTckfJZVfq8NmlnbLPM6KqGexuD8=";
    public static final long EXPIRE_DAYS = 0;
    public static final long EXPIRE_HOURS = 0;
    public static final long EXPIRE_MINUTES = 10;

    private static SecretKey generateKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY_BASE64);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private static Date toExpireDate(Date start) {
        LocalDateTime dateTime = start.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime end = dateTime.plusDays(EXPIRE_DAYS).plusHours(EXPIRE_HOURS).plusMinutes(EXPIRE_MINUTES);
        return Date.from(end.atZone(java.time.ZoneId.systemDefault()).toInstant());
    }

    public static JwtToken createToken(String email) {
        Date issuedAt = new Date();
        Date limit = toExpireDate(issuedAt);

        SecretKey key = generateKey();

        String token = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(email)
                .setIssuedAt(issuedAt)
                .setExpiration(limit)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        log.info("Token created: {}", token);
        return new JwtToken(token);
    }

    private static Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(generateKey())
                    .build()
                    .parseClaimsJws(refactorToken(token))
                    .getBody();
        } catch (Exception e) {
            log.error("Error parsing token: {}", e.getMessage());
            return null;
        }
    }

    public static String getEmailFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return (claims != null) ? claims.getSubject() : null;
    }

    public static boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(generateKey())
                    .build()
                    .parseClaimsJws(refactorToken(token));
            return true;
        } catch (JwtException ex) {
            log.error("Invalid token: {}", ex.getMessage());
            return false;
        }
    }

    static String refactorToken(String token) {
        if (token.startsWith(JWT_BEARER)) {
            return token.substring(JWT_BEARER.length());
        }
        return token;
    }
}
