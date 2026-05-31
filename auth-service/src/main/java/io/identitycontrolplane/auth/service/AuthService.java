package io.identitycontrolplane.auth.service;

import io.identitycontrolplane.auth.dto.AuthResponse;
import io.identitycontrolplane.auth.dto.LoginRequest;
import io.identitycontrolplane.auth.dto.RefreshRequest;
import io.identitycontrolplane.auth.dto.RegisterRequest;
import io.identitycontrolplane.auth.exception.AccountDisabledException;
import io.identitycontrolplane.auth.exception.BadCredentialsException;
import io.identitycontrolplane.auth.exception.TokenExpiredException;
import io.identitycontrolplane.auth.exception.TokenReuseDetectedException;
import io.identitycontrolplane.auth.exception.UserAlreadyExistsException;
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
            throw new UserAlreadyExistsException(request.getEmail());
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Default role USER not found"));

        User newUser = new User();
        newUser.setId(UUID.randomUUID());
        newUser.setEmail(request.getEmail());
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        newUser.setStatus(UserStatus.ACTIVE);
        newUser.setRoles(new HashSet<>());
        newUser.getRoles().add(userRole);

        userRepository.save(newUser);

        return issueTokenFamily(newUser);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(BadCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException();
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AccountDisabledException();
        }

        return issueTokenFamily(user);
    }

    public AuthResponse refresh(RefreshRequest request) {
        String tokenHash = cryptoUtil.sha256(request.getRefreshToken());

        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(BadCredentialsException::new);

        // Reuse detection: token was already rotated (revoked by a prior refresh).
        // This means either a replay attack or a stolen token was used first.
        // Nuke the entire family — every session from this login is now untrusted.
        if (refreshToken.getRevokedAt() != null) {
            refreshTokenRepository.revokeAllByFamilyId(refreshToken.getFamilyId(), LocalDateTime.now());
            throw new TokenReuseDetectedException();
        }

        // Absolute expiry check: the family-level expiry set at login time.
        // Rotation does NOT extend this — no sliding window.
        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException();
        }

        // Rotate: mark old token dead before issuing new one.
        refreshToken.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(refreshToken);

        // New token inherits the SAME expiresAt from the parent — absolute expiry
        // enforced.
        return issueRotatedToken(refreshToken.getUser(), refreshToken.getFamilyId(), refreshToken.getExpiresAt());
    }

    public void logout(String rawRefreshToken) {
        String tokenHash = cryptoUtil.sha256(rawRefreshToken);

        refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(token -> {
            // Revoke the entire family so all devices/tabs from this login are logged out.
            refreshTokenRepository.revokeAllByFamilyId(token.getFamilyId(), LocalDateTime.now());
        });
        // Intentionally silent if token not found — logout should always succeed from
        // client's perspective.
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Issues a brand-new token family (new familyId, fresh absolute expiry).
     * Called on login and register.
     */
    private AuthResponse issueTokenFamily(User user) {
        LocalDateTime absoluteExpiry = LocalDateTime.now().plusDays(refreshTokenExpiryDays);
        return issueRotatedToken(user, UUID.randomUUID(), absoluteExpiry);
    }

    /**
     * Issues a new refresh token within an existing family.
     * Inherits familyId and expiresAt from the parent — expiry never slides.
     */
    private AuthResponse issueRotatedToken(User user, UUID familyId, LocalDateTime expiresAt) {
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
        refreshToken.setFamilyId(familyId);
        refreshToken.setExpiresAt(expiresAt);
        refreshToken.setCreatedAt(LocalDateTime.now());

        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(accessToken, rawRefreshToken, "Bearer");
    }
}
