package com.employee.workwave.config.Auth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.employee.workwave.User.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtCookieFilter extends OncePerRequestFilter {
    @Autowired
    TokenProvider tokenService;

    @Autowired
    UserRepository userRepo;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String token = this.getToken(request);
        if (token != null) {
            String login = tokenService.validateToken(token);
            UserDetails user = userRepo.findByLogin(login); // get the userDetails
            Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            // creates an authentication object with the user's
            // details and authorities, which grants the user access.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response); // after the auth object is created the request continues as normal
    }

    private String getToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization"); // get the token from 'Authorization' header of request
        if (token == null)
            return null;
        return token.replace("Bearer ", ""); // clean the string
    }
}
