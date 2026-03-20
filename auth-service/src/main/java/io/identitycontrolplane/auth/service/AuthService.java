package io.identitycontrolplane.auth.service;

import io.identitycontrolplane.auth.dto.RegisterRequest;
import io.identitycontrolplane.auth.model.Role;
import io.identitycontrolplane.auth.model.User;
import io.identitycontrolplane.auth.model.UserStatus;
import io.identitycontrolplane.auth.repository.RoleRepository;
import io.identitycontrolplane.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Default role USER not found"));

        User newUser = new User();
        newUser.setId(UUID.randomUUID());
        newUser.setEmail(request.getEmail());

        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        newUser.setStatus(UserStatus.ACTIVE);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        newUser.setRoles(new HashSet<>());
        newUser.getRoles().add(userRole);

        userRepository.save(newUser);
    }
}
