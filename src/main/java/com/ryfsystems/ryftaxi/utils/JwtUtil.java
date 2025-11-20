package com.ryfsystems.ryftaxi.utils;

import com.ryfsystems.ryftaxi.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {

    private final JwtProperties jwt;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwt.getSecretKey().getBytes());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        claims.put("authorities", authorities);
        claims.put("username", userDetails.getUsername());

        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuer(jwt.getIssuer())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwt.getExpirationMs()))
                .signWith(getSigningKey())
                .compact();
    }

    public List<GrantedAuthority> extractAuthorities(String token) {
        try {
            Claims claims = extractAllClaims(token);
            @SuppressWarnings("unchecked")
            List<String> authorities = (List<String>) claims.get("authorities");

            if (authorities != null) {
                return authorities.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
            }
            return new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("❌ JWT token expirado: " + e.getMessage());
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("❌ JWT token inválido: " + e.getMessage());
            return false;
        }
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            List<GrantedAuthority> tokenAuthorities = extractAuthorities(token);
            List<String> tokenAuthorityStrings = tokenAuthorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
            List<String> userAuthorityStrings = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            return (username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token) &&
                    new HashSet<>(tokenAuthorityStrings).containsAll(userAuthorityStrings));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
