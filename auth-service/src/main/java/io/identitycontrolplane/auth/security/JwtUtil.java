package io.identitycontrolplane.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class JwtUtil {

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;
    private final String keyId;
    private final long accessTokenExpirySeconds;

    public JwtUtil(@Value("${jwt.access-token-expiry-seconds}") long accessTokenExpirySeconds) {
        this.accessTokenExpirySeconds = accessTokenExpirySeconds;
        this.keyId = UUID.randomUUID().toString();
        KeyPair keyPair = generateRsaKeyPair();
        this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
        this.publicKey  = (RSAPublicKey)  keyPair.getPublic();
    }

    private KeyPair generateRsaKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("RSA not available", e);
        }
    }

    public String generateAccessToken(UUID userId, String email, List<String> roles) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpirySeconds * 1000);

        return Jwts.builder()
                .header().keyId(keyId).and()
                .subject(userId.toString())
                .claim("email", email)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(privateKey)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public RSAPublicKey getPublicKey() { return publicKey; }
    public String getKeyId()          { return keyId; }
}
