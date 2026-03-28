package io.identitycontrolplane.auth.service;

import io.identitycontrolplane.auth.dto.AuthResponse;
import io.identitycontrolplane.auth.dto.LoginRequest;
import io.identitycontrolplane.auth.dto.RegisterRequest;
import io.identitycontrolplane.auth.model.RefreshToken;
import io.identitycontrolplane.auth.model.Role;
import io.identitycontrolplane.auth.model.User;
import io.identitycontrolplane.auth.model.UserStatus;
import io.identitycontrolplane.auth.repository.RefreshTokenRepository;
import io.identitycontrolplane.auth.repository.RoleRepository;
import io.identitycontrolplane.auth.repository.UserRepository;
import io.identitycontrolplane.auth.security.CryptoUtil;
import io.identitycontrolplane.auth.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CryptoUtil cryptoUtil;
    private final long refreshTokenExpiryDays;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            CryptoUtil cryptoUtil,
            @Value("${jwt.refresh-token-expiry-days}") long refreshTokenExpiryDays) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.cryptoUtil = cryptoUtil;
        this.refreshTokenExpiryDays = refreshTokenExpiryDays;
    }

    public AuthResponse register(RegisterRequest request) {
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

        return issueTokens(newUser);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalStateException("Account is disabled");
        }

        return issueTokens(user);
    }

    private AuthResponse issueTokens(User user) {
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), roles);

        String rawRefreshToken = UUID.randomUUID().toString();
        String tokenHash = cryptoUtil.sha256(rawRefreshToken);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(UUID.randomUUID());
        refreshToken.setUser(user);
        refreshToken.setTokenHash(tokenHash);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(refreshTokenExpiryDays));
        refreshToken.setCreatedAt(LocalDateTime.now());

        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(accessToken, rawRefreshToken, "Bearer");
    }
}
