package com.luizalabs.wishlist_service.adapters.in.web;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtTestUtils {
    private static final String SECRET = System.getenv("JWT_TEST_SECRET") != null
            ? System.getenv("JWT_TEST_SECRET")
            : "01234567890123456789012345678901";
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    public static String generateToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600_000))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }
}

