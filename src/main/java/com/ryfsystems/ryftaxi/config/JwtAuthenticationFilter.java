package com.ryfsystems.ryftaxi.config;

import com.ryfsystems.ryftaxi.utils.JwtUtil;
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

        String username = jwtUtil.extractUsername(jwt);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                List<GrantedAuthority> authorities = jwtUtil.extractAuthorities(jwt);

                if (Boolean.TRUE.equals(jwtUtil.validateToken(jwt))) {
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
                }
            } catch (Exception e) {
                logger.error("❌ JWT Authentication failed: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid JWT token");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}