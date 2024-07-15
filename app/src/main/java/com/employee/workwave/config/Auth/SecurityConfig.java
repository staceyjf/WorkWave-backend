package com.employee.workwave.config.Auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.DispatcherType;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

	@Autowired
	SecurityFilter JwtCookieFilter; // custom filter to extra and validate token

	@Autowired
	private AuthenticationManagerBuilder authenticationManagerBuilder;

	@Bean // AuthenticationManager is responsible for processing auth requests
	public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder,
			UserDetailsService userDetailsService)
			throws Exception {
		authenticationManagerBuilder
				.userDetailsService(userDetailsService)
				.passwordEncoder(bCryptPasswordEncoder);
		return authenticationManagerBuilder.build();
	}

	// configures the HTTP security
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()) // Cross-Site Request Forgery
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				// each session is authenticated independently
				.authorizeHttpRequests((authorize) -> authorize // configure URL-based auth
						.dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
						.requestMatchers("/admin/**").hasAnyRole("ADMIN")
						.requestMatchers("/swagger-ui/**").permitAll()
						.requestMatchers("/login").permitAll()
						.requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
						.anyRequest().authenticated());
		;

		return http.build();
	}

}
