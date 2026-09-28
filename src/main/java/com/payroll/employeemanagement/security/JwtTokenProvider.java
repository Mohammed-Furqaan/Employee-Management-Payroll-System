package com.payroll.employeemanagement.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utility component for generating, parsing, and validating JSON Web Tokens (JWT).
 * Utilizes io.jsonwebtoken version 0.12.6 APIs.
 */
@Component
public class JwtTokenProvider {

    // Default base64 encoded key (representing a 256-bit key for HMAC-SHA256)
    @Value("${app.jwt.secret:Y29tLnBheXJvbGwuZW1wbG95ZWVtYW5hZ2VtZW50Lmp3dC5zZWNyZXQucGhyYXNlLmtleS5mb3IuaG1hYy5zaGEyNTY=}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms:86400000}") // 24 Hours default
    private long jwtExpirationInMs;

    /**
     * Resolves the cryptographic signing key from the configured base64 secret.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a signed JWT for an authenticated user.
     * Contains the username (email) as subject and the user's role as a custom claim.
     */
    public String generateToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        String role = userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .findFirst()
                .orElse("ROLE_EMPLOYEE");

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Extracts the subject (username/email) from a validated JWT.
     */
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    /**
     * Validates an incoming JWT signature, expiration, and formatting.
     * Returns true if the token is valid, false otherwise.
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            // Token validation failed (expired, malformed, signature mismatch, etc.)
        }
        return false;
    }
}
