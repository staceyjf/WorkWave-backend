package com.employee.workwave.config.Auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.DispatcherType;

@Configuration
@EnableWebSecurity
// allows for different security based annotations
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

	@Autowired
	JwtCookieFilter jwtCookieFilter; // custom filter to extract JWT from cookie

	// interface that is used to auth user's credentials
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	// configures the HTTP security
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()) // Cross-Site Request Forgery
				// ensure all HTTP requests are redirecting to HTTPS
				// .requiresChannel(channel -> channel.anyRequest().requiresSecure())
				// each session is authenticated independently
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests((authorize) -> authorize // configure URL-based auth
						.dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
						.requestMatchers("/api-docs").permitAll()
						.requestMatchers("/swagger-ui/**").permitAll()
						.requestMatchers("/api/v1/users/register").permitAll()
						.requestMatchers("/api/v1/users/signin").permitAll()
						.requestMatchers("/api/v1/users/**").hasRole("ADMIN")
						.requestMatchers("/api/v1/departments").hasAnyRole("ADMIN", "USER")
						.requestMatchers("/api/v1/departments/**").hasRole("ADMIN")
						.requestMatchers("/api/v1/employee").hasAnyRole("ADMIN", "USER")
						.requestMatchers("/api/v1/employee/**").hasRole("ADMIN")
						.requestMatchers("/api/v1/contract").hasAnyRole("ADMIN", "USER")
						.requestMatchers("/api/v1/contract/**").hasRole("ADMIN")
						.anyRequest().authenticated())
				.addFilterBefore(jwtCookieFilter, UsernamePasswordAuthenticationFilter.class);
		;

		return http.build();
	}

	// interface for encoding passwords using BCrypt strong hashing function
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
