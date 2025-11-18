package com.example.salon_booking.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10) // run early, after Spring Security CORS processing
public class CorsDebugFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(CorsDebugFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String origin = request.getHeader("Origin");
        if (origin != null) {
            filterChain.doFilter(request, response); // allow CORS to be applied first by security
            String allowOrigin = response.getHeader("Access-Control-Allow-Origin");
            String method = request.getMethod();
            log.debug("CORS DEBUG | method={} path={} origin={} allowOrigin={} credentialsHeader={}", method,
                    request.getRequestURI(), origin, allowOrigin,
                    response.getHeader("Access-Control-Allow-Credentials"));
        } else {
            filterChain.doFilter(request, response);
        }
    }
}