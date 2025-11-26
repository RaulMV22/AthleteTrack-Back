package com.athletetrack.Security;

import com.athletetrack.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT (JSON Web Token) utility class for token generation and validation.
 * 
 * Configuration:
 * - jwt.secret: Secret key for signing tokens (must be at least 256 bits for
 * HS256)
 * - jwt.expiration: Token expiration time in milliseconds (default: 86400000 =
 * 24 hours)
 * 
 * Security Notes:
 * 1. The secret key should be a strong, random value in production
 * 2. Current implementation uses HS256 (HMAC with SHA-256)
 * 3. Tokens include user ID as subject and username/role as claims
 * 4. Tokens are stateless - no server-side session storage required
 * 
 * IMPORTANT: For production, change the default secret in
 * application.properties
 * to a cryptographically secure random string of at least 32 characters.
 */
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtUtil(@Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration:86400000}") long expirationMs) {
        // Use the configured secret; if short, Keys.hmacShaKeyFor will still accept it
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("username", user.getUsername())
                .claim("role", user.getRole() != null ? user.getRole().name() : "USER")
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        String sub = claims.getSubject();
        try {
            return Long.parseLong(sub);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
