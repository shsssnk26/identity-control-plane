package io.identitycontrolplane.auth.repository;

import io.identitycontrolplane.auth.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    /**
     * Revokes all tokens in a family by setting revoked_at.
     * Called when reuse is detected — the entire login session becomes untrusted.
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE RefreshToken rt SET rt.revokedAt = :now WHERE rt.familyId = :familyId AND rt.revokedAt IS NULL")
    void revokeAllByFamilyId(@Param("familyId") UUID familyId, @Param("now") LocalDateTime now);
}
