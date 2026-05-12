package com.uasz.gestion_suivi_evaluation_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims claims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return claims(token).getSubject();
    }

    public Long extractUserId(String token) {
        Object id = claims(token).get("id");

        if (id == null) {
            throw new RuntimeException("Le token JWT ne contient pas l'id utilisateur.");
        }

        if (id instanceof Integer i) return i.longValue();
        if (id instanceof Long l) return l;

        return Long.parseLong(id.toString());
    }

    public String extractRole(String token) {
        Object role = claims(token).get("role");
        return role == null ? "" : role.toString().replace("ROLE_", "");
    }

    public String extractNomComplet(String token) {
        Claims claims = claims(token);

        Object nom = claims.get("nom");
        Object prenom = claims.get("prenom");

        String n = nom == null ? "" : nom.toString();
        String p = prenom == null ? "" : prenom.toString();

        String nomComplet = (p + " " + n).trim();

        return !nomComplet.isBlank() ? nomComplet : extractUsername(token);
    }

    public boolean isTokenValid(String token) {
        try {
            Date expiration = claims(token).getExpiration();
            return expiration != null && expiration.after(new Date());
        } catch (Exception e) {
            System.out.println("JWT invalide : " + e.getMessage());
            return false;
        }
    }
}