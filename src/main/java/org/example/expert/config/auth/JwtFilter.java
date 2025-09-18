package org.example.expert.config.auth;

import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            getAuthorizationHeader(request)
                    .flatMap(this::getBearerToken)
                    .flatMap(jwtUtil::extractClaims)
                    .ifPresent(this::setUserAuthentication);
        } catch (Exception e) {
            log.warn("인증 오류 발생: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private void setUserAuthentication(Claims claims) {
        long userId = Long.parseLong(claims.getSubject());
        String email = claims.get("email", String.class);
        String profile = claims.get("profile", String.class);
        String userRoleString = claims.get("userRole", String.class);
        UserRole userRole = UserRole.of(userRoleString);
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(userRole.name()));
        AuthUser authUser = new AuthUser(userId, email, profile, userRole);
        Authentication token = new UsernamePasswordAuthenticationToken(authUser, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(token);
    }

    private Optional<String> getAuthorizationHeader(HttpServletRequest request) {
        return Optional.ofNullable((String) request.getAttribute(HttpHeaders.AUTHORIZATION));
    }

    private Optional<String> getBearerToken(@NonNull String authorizationHeader) {
        if (authorizationHeader.startsWith(JwtUtil.BEARER_PREFIX)) {
            return Optional.of(authorizationHeader.substring(JwtUtil.BEARER_PREFIX.length()));
        }
        return Optional.empty();
    }
}
