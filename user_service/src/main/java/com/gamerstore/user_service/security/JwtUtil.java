package com.gamerstore.user_service.security;

import java.util.Date;
import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private static final SecretKey SECRET_KEY =
            Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);

    private static final long EXPIRATION_TIME = 86400000; // 1 dia
    private static final long PASSWORD_RESET_EXPIRATION = 15 * 60 * 1000; // 15 minutos

    public static String gerarToken(Long usuarioId,
                                    String nomeUsuario,
                                    String email,
                                    String stripeCustomerId) {

        return Jwts.builder()
                .setSubject(usuarioId.toString())
                .claim("nome", nomeUsuario)
                .claim("email", email)
                .claim("stripeCustomerId", stripeCustomerId) // 🔥 NOVO
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String extrairToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token inválido ou ausente");
        }
        return authHeader.substring(7);
    }

    public static Long extrairUsuarioId(String token) {
        Claims claims = extrairClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    public static String extrairNomeUsuario(String token) {
        Claims claims = extrairClaims(token);
        return claims.get("nome", String.class);
    }

    public static String extrairEmail(String token) {
        Claims claims = extrairClaims(token);
        return claims.get("email", String.class);
    }

    // 🔥 NOVO MÉTODO
    public static String extrairStripeCustomerId(String token) {
        Claims claims = extrairClaims(token);
        return claims.get("stripeCustomerId", String.class);
    }

    private static Claims extrairClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generatePasswordResetToken(Long usuarioId) {
        return Jwts.builder()
                .setSubject(usuarioId.toString())
                .claim("type", "PASSWORD_RESET")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + PASSWORD_RESET_EXPIRATION))
                .signWith(SECRET_KEY)
                .compact();
    }

    public Long validatePasswordResetToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String type = claims.get("type", String.class);

        if (!"PASSWORD_RESET".equals(type)) {
            throw new RuntimeException("Token inválido para reset de senha");
        }

        return Long.parseLong(claims.getSubject());
    }
}