package com.ryfsystems.ryftaxi.config;

import com.ryfsystems.ryftaxi.utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);

        if (jwt.trim().isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        String username = null;

        try {
            // Intentar extraer el username PRIMERO para capturar excepciones temprano
            username = jwtUtil.extractUsername(jwt);
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token expirado para usuario: " + e.getClaims().getSubject());
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Token expirado. Por favor inicie sesión nuevamente.");
            return;
        } catch (Exception e) {
            logger.warn("Error extrayendo username del token: " + e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Token inválido");
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // Validar el token - esto puede lanzar ExpiredJwtException
                if (jwtUtil.validateToken(jwt)) {
                    List<GrantedAuthority> authorities = jwtUtil.extractAuthorities(jwt);

                    UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                            .username(username)
                            .password("") // No necesitamos password para JWT
                            .authorities(authorities)
                            .build();

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, authorities);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    logger.info("✅ JWT Authentication successful for: " + username +
                            " with authorities: " + authorities.stream()
                            .map(GrantedAuthority::getAuthority)
                            .toList());
                } else {
                    // Si validateToken devuelve false (pero no lanza excepción)
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                            "Token inválido");
                    return;
                }

            } catch (ExpiredJwtException e) {
                logger.warn("JWT token expirado para usuario: " + e.getClaims().getSubject());
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "Token expirado. Por favor inicie sesión nuevamente.");
                return;

            } catch (MalformedJwtException e) {
                logger.warn("JWT token malformado: " + e.getMessage());
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "Token malformado");
                return;

            } catch (SignatureException e) {
                logger.warn("JWT signature inválida: " + e.getMessage());
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "Firma del token inválida");
                return;

            } catch (IllegalArgumentException e) {
                logger.warn("JWT claims vacío: " + e.getMessage());
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "Token claims vacío");
                return;

            } catch (Exception e) {
                logger.error("Error inesperado procesando JWT: " + e.getMessage());
                sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Error interno del servidor");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = String.format(
                "{\"success\":false,\"message\":\"%s\",\"error\":\"%s\"}",
                message,
                getErrorType(status)
        );
        response.getWriter().write(jsonResponse);
    }

    private String getErrorType(int status) {
        switch (status) {
            case HttpServletResponse.SC_UNAUTHORIZED:
                return "UNAUTHORIZED";
            case HttpServletResponse.SC_FORBIDDEN:
                return "FORBIDDEN";
            case HttpServletResponse.SC_INTERNAL_SERVER_ERROR:
                return "INTERNAL_SERVER_ERROR";
            default:
                return "UNKNOWN_ERROR";
        }
    }
}