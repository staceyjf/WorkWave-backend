package com.employee.workwave.config.Auth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.employee.workwave.Employee.EmployeeRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtCookieFilter extends OncePerRequestFilter {
    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    EmployeeRepository userRepo;

    // intercept once per a request
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String token = extractJwtFromCookie(request);
        if (token != null && !token.isEmpty()) {
            String userID = tokenProvider.validateToken(token); // get the publicID string
            UserDetails user = userRepo.findByPublicId(userID); // get the user from the db
            // creates an authentication object with the user's
            // details and authorities, which grants the user access.
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user,
                    null, user.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request, response); // after the auth object is created the request continues as normal
    }

    private String extractJwtFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("accessToken")) {
                    // can only add checks for secure and httponly with the add of
                    // https
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
