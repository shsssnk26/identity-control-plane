package io.identitycontrolplane.auth.repository;

import io.identitycontrolplane.auth.model.RefreshToken;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
}
