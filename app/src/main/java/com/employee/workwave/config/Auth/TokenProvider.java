package com.employee.workwave.config.Auth;

import com.employee.workwave.Employee.Employee;
import com.employee.workwave.exceptions.ServiceValidationException;
import com.employee.workwave.exceptions.ValidationErrors;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

@Service
public class TokenProvider {
    @Value("${security.jwt.token.secret-key}")
    private String secretKey; // inject in env secret

    public String generateAccessToken(Employee user) throws ServiceValidationException {
        ValidationErrors errors = new ValidationErrors();

        if (secretKey == null) {
            errors.addError("User", "secretKey is null.");
            throw new ServiceValidationException(errors);
        }

        if (user == null) {
            errors.addError("User", "User is null.");
            throw new ServiceValidationException(errors);
        }

        if (user.getUsername() == null) {
            errors.addError("User", "Username is null.");
            throw new ServiceValidationException(errors);
        }

        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey); // uses Hash-based Message Auth Code

            return JWT.create()
                    .withSubject(user.getPublicId()) // represents who the token is associated with
                    .withClaim("role", user.getRole().name())
                    .withExpiresAt(genAccessExpirationDate()) // set expiry
                    .sign(algorithm); // verify that the token is genuine
        } catch (JWTCreationException ex) {
            errors.addError("User", "Invalid token.");
            throw new ServiceValidationException(errors);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            return JWT.require(algorithm)
                    .build() // return a verifier
                    .verify(token)
                    .getSubject(); // return the subject which is our publicId
        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException("Error while validating token", exception);
            // OncePerRequestFilter interface does not permit custom errors
        }
    }

    // set an expiry time for token
    private Instant genAccessExpirationDate() {
        return LocalDateTime.now(ZoneId.of("Australia/Sydney")).plusHours(2).toInstant(ZoneOffset.UTC);
    }
}
