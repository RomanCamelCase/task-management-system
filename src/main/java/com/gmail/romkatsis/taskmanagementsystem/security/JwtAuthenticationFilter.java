package com.gmail.romkatsis.taskmanagementsystem.security;

import com.gmail.romkatsis.taskmanagementsystem.enums.Role;
import com.gmail.romkatsis.taskmanagementsystem.exceptions.InvalidTokenException;
import com.gmail.romkatsis.taskmanagementsystem.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Autowired
    public JwtAuthenticationFilter(JwtUtils jwtUtils, HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtUtils = jwtUtils;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            Claims claims;
            try {
                claims = jwtUtils.getClaims(token);
            } catch (JwtException exception) {
                handlerExceptionResolver
                        .resolveException(request, response, null, new InvalidTokenException(exception.getMessage()));
                return;
            }

            String id = claims.getSubject();
            List<String> rolesList = claims.get("authorities", List.class);
            Set<Role> roles = rolesList.stream().map(Role::valueOf).collect(Collectors.toSet());

            UserDetails user = new User(id, "", roles);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user, null, roles);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }
}
