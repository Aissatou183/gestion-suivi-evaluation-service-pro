package com.uasz.gestion_suivi_evaluation_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public Long extractUserId(String token) {
        Object id = extractAllClaims(token).get("id");
        return Long.valueOf(id.toString());
    }

    public String extractNomComplet(String token) {
        Claims claims = extractAllClaims(token);
        String prenom = claims.get("prenom", String.class);
        String nom = claims.get("nom", String.class);
        return ((prenom == null ? "" : prenom) + " " + (nom == null ? "" : nom)).trim();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
