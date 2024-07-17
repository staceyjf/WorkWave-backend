package com.employee.workwave.User;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.employee.workwave.exceptions.ServiceValidationException;
import com.employee.workwave.exceptions.ValidationErrors;

import jakarta.validation.Valid;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository repo;

    private static final Logger fullLogsLogger = LogManager.getLogger("fullLogs");

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserDetails user = repo.findByUsername(username); // custom lookup
        return user;
    }

    public UserDetails register(@Valid RegisterDTO data) throws ServiceValidationException {
        ValidationErrors errors = new ValidationErrors();
        try {
            // check to see if the username already exists
            if (data.username() != null && repo.findByUsername(data.username()) != null) {
                errors.addError("User", "Username already exists");
                throw new ServiceValidationException(errors);
            }

            String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
            User newUser = new User(data.username(), encryptedPassword, data.role());
            User savedUser = repo.save(newUser);
            fullLogsLogger.info("New user saved to the db");
            return savedUser;
        } catch (Exception ex) {
            fullLogsLogger.error("An error occurred when trying to sign up and create a new user in the db: ",
                    ex.getLocalizedMessage());
            errors.addError("User", "An error occurred during sign up. Please try again");
            throw new ServiceValidationException(errors);
        }
    }

    public List<User> findAllUsers() {
        return this.repo.findAll();
    }

    public Optional<User> findById(Long id) {
        Optional<User> foundUser = this.repo.findById(id);
        fullLogsLogger.info("Located User in db with ID: " + foundUser.get().getId());
        return foundUser;
    }

}
