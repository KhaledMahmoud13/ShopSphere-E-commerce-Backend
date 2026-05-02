package com.khaled.shopsphere.security;

import com.khaled.shopsphere.exception.BusinessException;
import com.khaled.shopsphere.handler.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.khaled.shopsphere.exception.ErrorCode.INTERNAL_EXCEPTION;
import static com.khaled.shopsphere.exception.ErrorCode.USERNAME_NOT_FOUND;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;

    @Override
    protected void doFilterInternal(
            @NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final FilterChain filterChain) throws ServletException, IOException {

        if (request.getServletPath().contains("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            final String jwt;
            final String username;

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            jwt = authHeader.substring(7);
            username = this.jwtService.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                final UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (this.jwtService.isTokenValid(jwt, userDetails.getUsername())) {
                    final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (BusinessException e) {
            stashAndDelegate(request, response, e.getErrorCode().getCode(), e.getMessage());
        } catch (UsernameNotFoundException e) {
            stashAndDelegate(request, response,
                    USERNAME_NOT_FOUND.getCode(), USERNAME_NOT_FOUND.getDefaultMessage());
        } catch (Exception e) {
            log.error("Unexpected error in JWT filter", e);
            stashAndDelegate(request, response,
                    INTERNAL_EXCEPTION.getCode(), INTERNAL_EXCEPTION.getDefaultMessage());
        }
    }

    private void stashAndDelegate(
            HttpServletRequest request,
            HttpServletResponse response,
            String code,
            String message
    ) throws IOException {
        final ErrorResponse body = ErrorResponse.builder()
                .message(message)
                .code(code)
                .build();
        request.setAttribute("security.error", body);
        jwtAuthEntryPoint.commence(request, response,
                new InsufficientAuthenticationException(message));
    }
}
