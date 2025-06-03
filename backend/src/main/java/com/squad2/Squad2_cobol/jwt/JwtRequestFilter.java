package com.squad2.Squad2_cobol.jwt;

import com.squad2.Squad2_cobol.services.BlackListService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUserDetailService detailsService;
    private final BlackListService blacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path.startsWith("/auth/") ||
                path.equals("/user/create") ||
                path.equals("/user/test") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs/")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui") || path.equals("/swagger-ui.html")
                || path.equals("/api/v1/auth/login") || path.equals("/api/v1/customers/create")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = request.getHeader(JwtUtill.JWT_AUTHORIZATION);

        if (token == null || !token.startsWith(JwtUtill.JWT_BEARER)) {
            log.info("JWT Token is null, empty or not starting with 'Bearer '");
            filterChain.doFilter(request, response);
            return;
        }

        String validToken = JwtUtill.refactorToken(token);

        if (blacklistService.isTokenBlacklisted(validToken)) {
            log.warn("JWT Token is blacklisted.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has been blacklisted");
            return;
        }

        if (!JwtUtill.isTokenValid(token)) {
            log.warn("JWT Token is invalid or expired.");
            filterChain.doFilter(request, response);
            return;
        }

        String email = JwtUtill.getEmailFromToken(token);
        toAuthentication(request, email);
        filterChain.doFilter(request, response);
    }


    private void toAuthentication(HttpServletRequest request, String email) {
        UserDetails userDetails = detailsService.loadUserByUsername(email);

        UsernamePasswordAuthenticationToken authenticationToken =
                UsernamePasswordAuthenticationToken.authenticated(
                        userDetails, null, userDetails.getAuthorities());

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
